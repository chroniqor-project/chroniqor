## Getting started

### Requirements

- Docker Desktop ([installation](https://www.docker.com/products/docker-desktop/))
- [Visual Studio Code](https://code.visualstudio.com/)
- [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

### Setup

1. Start Docker Desktop.
2. Open the Chroniqor repository in Visual Studio Code.
3. Create `.env` from `.env.example` if required:

   ```powershell
   Copy-Item .env.example .env
   ```

4. Run `Dev Containers: Reopen in Container` from the Command Palette.
5. Wait for the Dev Container and PostgreSQL service to become ready.
6. Verify Docker from the integrated terminal:

   ```bash
   docker version
   docker info
   ```

7. Start the application:

   ```bash
   ./gradlew :chroniqor-runtime:bootRun
   ```

## Running the checks

Inside the Dev Container, run:

```bash
./gradlew clean check
```

This runs the project build, tests, architecture checks and formatting checks.

## Troubleshooting

### The Dev Container does not start

Make sure Docker Desktop is running and rebuild the Dev Container:

```text
Dev Containers: Rebuild Container Without Cache
```

Check the services with:

```bash
docker compose ps
```

PostgreSQL should be running and healthy.

If the application does not start, verify PostgreSQL and run:

```bash
./gradlew :chroniqor-runtime:bootRun
```

## Running without the Dev Container

The Dev Container is optional. Java 21 and Docker Desktop are still required
for PostgreSQL and Testcontainers.

On Windows:

```powershell
.\gradlew.bat :chroniqor-runtime:bootRun
```

On Linux and macOS:

```bash
./gradlew :chroniqor-runtime:bootRun
```
