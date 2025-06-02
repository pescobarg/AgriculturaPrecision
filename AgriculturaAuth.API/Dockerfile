FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /app

# Copiar todo (no solo csproj)
COPY . ./

# Restaurar y construir
RUN dotnet restore
RUN dotnet publish -c Release -o out

# Ver contenido publicado (solo para debug, puedes quitarlo luego)
RUN ls -l out

FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app

COPY --from=build /app/out ./

EXPOSE 5000

ENTRYPOINT ["dotnet", "AgriculturaAuth.API.dll"]
