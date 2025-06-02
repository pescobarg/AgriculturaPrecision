namespace AgriculturaAuth.API.Models
{
    public class LoginRequest
    {
        public string Username { get; set; }
        public string Password { get; set; }
    }

    public class RegisterRequest
    {
        public string Username { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Telefono { get; set; }
        public string Role { get; set; } // ADMINISTRADOR, PROVEEDOR, AGRICULTOR, EXPERTO
    }

    public class AuthResponse
    {
        public string AccessToken { get; set; }
        public string RefreshToken { get; set; }
        public int ExpiresIn { get; set; }
        public string TokenType { get; set; }
        public UserInfo User { get; set; }
    }

    public class UserInfo
    {
        public string Id { get; set; }
        public string Username { get; set; }
        public string Email { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Telefono { get; set; }
        public List<string> Roles { get; set; }
    }

    public class KeycloakTokenResponse
    {
        public string access_token { get; set; }
        public string refresh_token { get; set; }
        public int expires_in { get; set; }
        public string token_type { get; set; }
    }

    public class KeycloakUserRepresentation
    {
        public string id { get; set; }
        public string username { get; set; }
        public string email { get; set; }
        public string firstName { get; set; }
        public string lastName { get; set; }
        public bool enabled { get; set; }
        public Dictionary<string, List<string>> attributes { get; set; }
        public List<KeycloakCredential> credentials { get; set; }
        public List<string> realmRoles { get; set; }
    }

    public class KeycloakCredential
    {
        public string type { get; set; }
        public string value { get; set; }
        public bool temporary { get; set; }
    }
}