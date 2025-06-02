using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Configuración de CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAngularApp", policy =>
    {
        policy.WithOrigins("http://localhost:4200")
              .AllowAnyHeader()
              .AllowAnyMethod()
              .AllowCredentials();
    });
});

// Configuración de HttpClient para Keycloak
builder.Services.AddHttpClient("keycloak", client =>
{
    client.BaseAddress = new Uri("http://localhost:8080/");
});

// Registrar servicios
builder.Services.AddScoped<AgriculturaAuth.API.Services.IAuthService, AgriculturaAuth.API.Services.AuthService>();

// Configuración de autenticación JWT
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.Authority = "http://localhost:8080/realms/agricultura-precision";
        options.Audience = "agricultura-backend";
        options.RequireHttpsMetadata = false; // Solo para desarrollo
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = false,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ClockSkew = TimeSpan.Zero
        };
    });

builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("AdminOnly", policy => policy.RequireClaim("realm_access", "ADMINISTRADOR"));
    options.AddPolicy("ProveedorOnly", policy => policy.RequireClaim("realm_access", "PROVEEDOR"));
    options.AddPolicy("AgricultorOnly", policy => policy.RequireClaim("realm_access", "AGRICULTOR"));
    options.AddPolicy("ExpertoOnly", policy => policy.RequireClaim("realm_access", "EXPERTO"));
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("AllowAngularApp");

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();