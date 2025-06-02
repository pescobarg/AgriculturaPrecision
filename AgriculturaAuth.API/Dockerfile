# Etapa de compilación
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copiar los archivos del proyecto
COPY ["AgriculturaAuth.API.csproj", "./"]
RUN dotnet restore "AgriculturaAuth.API.csproj"

# Copiar el resto de archivos y compilar
COPY . .
RUN dotnet publish "AgriculturaAuth.API.csproj" -c Release -o /app/publish

# Etapa de ejecución
FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app

COPY --from=build /app/publish .

# Expone el puerto 80 para HTTP (ajústalo si usas otro)
EXPOSE 8083

ENV ASPNETCORE_URLS=http://+:80

ENTRYPOINT ["dotnet", "AgriculturaAuth.API.dll"]
