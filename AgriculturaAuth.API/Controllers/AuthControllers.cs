using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using AgriculturaAuth.API.Models;
using AgriculturaAuth.API.Services;

namespace AgriculturaAuth.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            try
            {
                if (string.IsNullOrEmpty(request.Username) || string.IsNullOrEmpty(request.Password))
                {
                    return BadRequest("Username and password are required");
                }

                var result = await _authService.LoginAsync(request);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterRequest request)
        {
            try
            {
                if (string.IsNullOrEmpty(request.Username) || 
                    string.IsNullOrEmpty(request.Password) || 
                    string.IsNullOrEmpty(request.Email) ||
                    string.IsNullOrEmpty(request.Role))
                {
                    return BadRequest("All fields are required");
                }

                var validRoles = new[] { "ADMINISTRADOR", "PROVEEDOR", "AGRICULTOR", "EXPERTO" };
                if (!validRoles.Contains(request.Role))
                {
                    return BadRequest("Invalid role specified");
                }

                var result = await _authService.RegisterAsync(request);
                
                if (result)
                {
                    return Ok(new { message = "User registered successfully" });
                }
                
                return BadRequest(new { message = "Registration failed" });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpGet("userinfo")]
        [Authorize]
        public async Task<IActionResult> GetUserInfo()
        {
            try
            {
                var token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
                var userInfo = await _authService.GetUserInfoAsync(token);
                return Ok(userInfo);
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpGet("dashboard")]
        [Authorize]
        public IActionResult GetDashboard()
        {
            var userRoles = User.Claims
                .Where(c => c.Type == "realm_access")
                .Select(c => c.Value)
                .FirstOrDefault();

            if (string.IsNullOrEmpty(userRoles))
            {
                return Unauthorized();
            }

            // Determinar dashboard según el rol
            if (userRoles.Contains("ADMINISTRADOR"))
            {
                return Ok(new { dashboard = "admin", message = "Welcome Administrator" });
            }
            else if (userRoles.Contains("PROVEEDOR"))
            {
                return Ok(new { dashboard = "proveedor", message = "Welcome Provider" });
            }
            else if (userRoles.Contains("AGRICULTOR"))
            {
                return Ok(new { dashboard = "agricultor", message = "Welcome Farmer" });
            }
            else if (userRoles.Contains("EXPERTO"))
            {
                return Ok(new { dashboard = "experto", message = "Welcome Expert" });
            }

            return Ok(new { dashboard = "default", message = "Welcome User" });
        }

        [HttpPost("logout")]
        [Authorize]
        public IActionResult Logout()
        {
            // En una implementación real, aquí invalidarías el token en Keycloak
            return Ok(new { message = "Logged out successfully" });
        }
    }
}