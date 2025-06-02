public class ApplicationUser
{
    public int Id { get; set; } // PK autoincremental
    public string Username { get; set; } = null!;
    public string Email { get; set; } = null!;
    public string FirstName { get; set; } = null!;
    public string LastName { get; set; } = null!;
    public string Telefono { get; set; } = null!;
    public string Role { get; set; } = null!;
}
