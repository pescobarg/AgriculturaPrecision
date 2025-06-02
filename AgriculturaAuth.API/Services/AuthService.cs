using AgriculturaAuth.API.Models;
using Newtonsoft.Json;
using System.Text;
using System.IdentityModel.Tokens.Jwt;

namespace AgriculturaAuth.API.Services
{
    public interface IAuthService
    {
        Task<AuthResponse> LoginAsync(LoginRequest request);
        Task<bool> RegisterAsync(RegisterRequest request);
        Task<UserInfo> GetUserInfoAsync(string token);
    }

    public class AuthService : IAuthService
    {
        private readonly HttpClient _httpClient;
        private readonly IConfiguration _configuration;
        private readonly string _keycloakBaseUrl;
        private readonly string _realm;
        private readonly string _clientId;
        private readonly string _clientSecret;
        private readonly ApplicationDbContext _dbContext;



        public AuthService(IHttpClientFactory httpClientFactory, IConfiguration configuration, ApplicationDbContext dbContext)
        {
            _httpClient = httpClientFactory.CreateClient("keycloak");
            _configuration = configuration;
            _keycloakBaseUrl = "http://localhost:8080";
            _realm = "agricultura-precision";
            _clientId = "agricultura-backend";
            _clientSecret = "Uy8TkVCSSFPuuPESrM4g0GT0PzLiuxlg";
            _dbContext = dbContext;

        }

        public async Task<AuthResponse> LoginAsync(LoginRequest request)
        {
            var tokenEndpoint = $"{_keycloakBaseUrl}/realms/{_realm}/protocol/openid-connect/token";

            var formParams = new List<KeyValuePair<string, string>>
            {
                new("grant_type", "password"),
                new("client_id", _clientId),
                new("client_secret", _clientSecret),
                new("username", request.Username),
                new("password", request.Password),
                new("scope", "openid")
            };

            var formContent = new FormUrlEncodedContent(formParams);
            var response = await _httpClient.PostAsync(tokenEndpoint, formContent);

            if (!response.IsSuccessStatusCode)
            {
                throw new Exception("Invalid credentials");
            }

            var content = await response.Content.ReadAsStringAsync();
            var tokenResponse = JsonConvert.DeserializeObject<KeycloakTokenResponse>(content);

            var userInfo = await GetUserInfoAsync(tokenResponse.access_token);

            return new AuthResponse
            {
                AccessToken = tokenResponse.access_token,
                RefreshToken = tokenResponse.refresh_token,
                ExpiresIn = tokenResponse.expires_in,
                TokenType = tokenResponse.token_type,
                User = userInfo
            };
        }
        public async Task<bool> RegisterAsync(RegisterRequest request)
        {
            // Obtener token admin
            var adminToken = await GetAdminTokenAsync();

            // Endpoint para crear usuario en Keycloak
            var userEndpoint = $"{_keycloakBaseUrl}/admin/realms/{_realm}/users";

            var keycloakUser = new KeycloakUserRepresentation
            {
                username = request.Username,
                email = request.Email,
                firstName = request.FirstName,
                lastName = request.LastName,
                enabled = true,
                attributes = new Dictionary<string, List<string>>
        {
            { "telefono", new List<string> { request.Telefono } }
        },
                credentials = new List<KeycloakCredential>
        {
            new KeycloakCredential
            {
                type = "password",
                value = request.Password,
                temporary = false
            }
        },
                realmRoles = new List<string> { request.Role }
            };

            var json = JsonConvert.SerializeObject(keycloakUser);
            var content = new StringContent(json, Encoding.UTF8, "application/json");

            _httpClient.DefaultRequestHeaders.Authorization =
                new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", adminToken);

            var response = await _httpClient.PostAsync(userEndpoint, content);

            if (response.IsSuccessStatusCode || response.StatusCode == System.Net.HttpStatusCode.Created)
            {
                // Obtener Id de usuario creado en Keycloak
                var userId = await GetUserIdByUsernameAsync(request.Username, adminToken);
                if (!string.IsNullOrEmpty(userId))
                {
                    // Asignar rol al usuario en Keycloak
                    await AssignRoleToUserAsync(userId, request.Role, adminToken);

                    // Guardar usuario en base de datos local
                    var nuevoUsuario = new ApplicationUser
                    {
                        Username = request.Username,
                        Email = request.Email,
                        FirstName = request.FirstName,
                        LastName = request.LastName,
                        Telefono = request.Telefono,
                        Role = request.Role
                    };

                    _dbContext.Users.Add(nuevoUsuario);
                    await _dbContext.SaveChangesAsync();

                    return true;
                }
            }

            // Si algo falla
            return false;
        }


        public async Task<UserInfo> GetUserInfoAsync(string token)
        {
            var userInfoEndpoint = $"{_keycloakBaseUrl}/realms/{_realm}/protocol/openid-connect/userinfo";

            _httpClient.DefaultRequestHeaders.Authorization =
                new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", token);

            var response = await _httpClient.GetAsync(userInfoEndpoint);

            if (!response.IsSuccessStatusCode)
            {
                throw new Exception("Failed to get user info");
            }

            var content = await response.Content.ReadAsStringAsync();
            var userInfoData = JsonConvert.DeserializeObject<Dictionary<string, object>>(content);

            // Extraer roles del token JWT
            var handler = new JwtSecurityTokenHandler();
            var jsonToken = handler.ReadJwtToken(token);
            var realmAccess = jsonToken.Claims.FirstOrDefault(c => c.Type == "realm_access")?.Value;
            var roles = new List<string>();

            if (!string.IsNullOrEmpty(realmAccess))
            {
                var realmAccessObj = JsonConvert.DeserializeObject<Dictionary<string, List<string>>>(realmAccess);
                if (realmAccessObj.ContainsKey("roles"))
                {
                    roles = realmAccessObj["roles"];
                }
            }

            return new UserInfo
            {
                Id = userInfoData.ContainsKey("sub") ? userInfoData["sub"].ToString() : "",
                Username = userInfoData.ContainsKey("preferred_username") ? userInfoData["preferred_username"].ToString() : "",
                Email = userInfoData.ContainsKey("email") ? userInfoData["email"].ToString() : "",
                FirstName = userInfoData.ContainsKey("given_name") ? userInfoData["given_name"].ToString() : "",
                LastName = userInfoData.ContainsKey("family_name") ? userInfoData["family_name"].ToString() : "",
                Telefono = userInfoData.ContainsKey("telefono") ? userInfoData["telefono"].ToString() : "",
                Roles = roles
            };
        }

        private async Task<string> GetAdminTokenAsync()
        {
            var tokenEndpoint = $"{_keycloakBaseUrl}/realms/master/protocol/openid-connect/token";

            var formParams = new List<KeyValuePair<string, string>>
            {
                new("grant_type", "password"),
                new("client_id", "admin-cli"),
                new("username", "admin"),
                new("password", "admin")
            };

            var formContent = new FormUrlEncodedContent(formParams);
            var response = await _httpClient.PostAsync(tokenEndpoint, formContent);

            var content = await response.Content.ReadAsStringAsync();
            var tokenResponse = JsonConvert.DeserializeObject<KeycloakTokenResponse>(content);

            return tokenResponse.access_token;
        }

        private async Task<string> GetUserIdByUsernameAsync(string username, string adminToken)
        {
            var usersEndpoint = $"{_keycloakBaseUrl}/admin/realms/{_realm}/users?username={username}";

            _httpClient.DefaultRequestHeaders.Authorization =
                new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", adminToken);

            var response = await _httpClient.GetAsync(usersEndpoint);
            var content = await response.Content.ReadAsStringAsync();
            var users = JsonConvert.DeserializeObject<List<KeycloakUserRepresentation>>(content);

            return users?.FirstOrDefault()?.id;
        }

        private async Task<bool> AssignRoleToUserAsync(string userId, string roleName, string adminToken)
        {
            // Primero obtener el rol
            var rolesEndpoint = $"{_keycloakBaseUrl}/admin/realms/{_realm}/roles/{roleName}";

            _httpClient.DefaultRequestHeaders.Authorization =
                new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", adminToken);

            var roleResponse = await _httpClient.GetAsync(rolesEndpoint);
            var roleContent = await roleResponse.Content.ReadAsStringAsync();
            var role = JsonConvert.DeserializeObject<object>(roleContent);

            // Asignar rol al usuario
            var assignRoleEndpoint = $"{_keycloakBaseUrl}/admin/realms/{_realm}/users/{userId}/role-mappings/realm";
            var roleArray = new[] { role };
            var json = JsonConvert.SerializeObject(roleArray);
            var content = new StringContent(json, Encoding.UTF8, "application/json");

            var assignResponse = await _httpClient.PostAsync(assignRoleEndpoint, content);
            return assignResponse.IsSuccessStatusCode;
        }
    }
}