# Islands

## Build and run

With Docker
```bash
./docker-build.sh
docker compose up
```

##  Testing
Navigate to [http://localhost:8080/openapi/ui/index.html#/](http://localhost:8080/openapi/ui/index.html#/) to test out the endpoints and run the datasets found in the `examples/` directory.


## Local development

1. Open the project folder in VS Code:
    ```bash
    code /home/ravid/workspaces/Island-Router
    ```

2. Install the [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) extension for Visual Studio Code.


3. Reopen the folder in a Dev Container:
    - Press `F1` to open the Command Palette.
    - Type and select `Remote-Containers: Reopen in Container`.

4. Wait for the container to build and start. Once ready, the project will open inside the Dev Container environment.

5. All of the required dependencies will be installed automatically.

6. You can use `mvnd` instead of `mvn` for quicker builds, as well as the Helidon CLI for managing the project.

## Running and debugging

Debugging Options:

This code can be debugged in three different ways:

1. **Using the Built-in Debug Configuration**:
   - Utilize the IDE's built-in debug configuration to launch the application.

2. **Running `helidon dev --project island`**:
   - Use the Helidon CLI command `helidon dev --project island` to start the application in development mode with debugging enabled.

3. **Using the Preconfigured Task**:
   - A preconfigured task is available in the development environment (e.g., in `.vscode/tasks.json` or similar configuration files).
   - This task is set up to streamline the debugging process by automating the necessary steps to attach a debugger.
   - To use this task:
        - Open the command palette in your IDE (e.g., VS Code).
        - Search for and run the preconfigured task called `helidon-dev`
        - This will start the application, you can now attach the debugger to the running java process.

## Modular Architecture

This repository is designed to serve as a base for extending the project into a modular microservices architecture. The current structure demonstrates how to manage multiple submodules within a single repository while sharing common code and configurations.

Key principles of the modular architecture:

1. **Lightweight Microservices**:
    - Each microservice, such as `island`, is designed to be lightweight and focused on a specific domain or functionality.
    - Services interact with external systems (e.g., caches, databases, or other microservices) rather than storing data locally.

2. **Shared Code and Configurations**:
    - Common aspects of the codebase, such as utilities, configurations, and shared libraries, are centralized to avoid duplication and ensure consistency across services.

3. **Extensibility**:
    - The repository structure allows for easy addition of new microservices as submodules, following the same conventions and leveraging shared resources.

### Example Structure for Multiple Microservices

- `core/`: Shared libraries and utilities used across all microservices.

- `island/`: Lightweight microservice for managing the `visitCheck` interface.

- `another-service/`: Placeholder for a future microservice.

By following this modular approach, the project can scale efficiently while maintaining a clean and organized codebase.


### OpenAPI Generator in the Core Module

The `core/` module leverages the OpenAPI Generator to streamline the creation of client libraries, server stubs, and API documentation. This tool ensures consistency between the API specification and the generated code, reducing manual effort and potential errors.

#### Usage

1. **OpenAPI Specification**:
    - The OpenAPI specification file (`model.yaml`) is located in the `core/` module, within the `resources/swagger` folder.
    - This file defines the API endpoints, request/response models, and other relevant details.

2. **Generating Code**:
    - Use the OpenAPI Generator Maven plugin configured in the `pom.xml` of the `core/` module to 
    - Running `mvn compile` generates the DTO and Server/Client classes
    - The generated code will be placed in the `target/generated-sources` directory within the `core/` module.

3. **Integration**:
    - The generated code is integrated into the `core/` module and shared across other microservices, ensuring a single source of truth for API definitions.

By using the OpenAPI Generator, the project maintains a robust and scalable approach to API development.

## Repo structure

The project is organized into multiple directories and files, each serving a specific purpose:

- `.devcontainer/`: Contains configuration files for the development container, including Dockerfiles, scripts, and environment variables.
    - `devcontainer.json`: Defines the development container configuration.
    - `install-helidon.sh`: Script to install Helidon in the container.
    - `docker-compose.yml`: Docker Compose configuration for the development environment.

- `.vscode/`: Contains IDE-specific configuration files.
    - `launch.json`: Debug configurations for the project.
    - `tasks.json`: Preconfigured tasks for building, running, and debugging the application.

- `*examples/`: Includes example datasets for testing and development.
    - `dataset-good.json`: A valid dataset example.
    - `dataset-looping.json`: A dataset example with looping data.

- `core/`: Core functionality of the application.

- `island/`: Api microservice for managing the visitCheck interface

- `docker-bake.hcl`: Configuration file for Docker Buildx to manage multi-target builds.

- `docker-compose.yaml`: Docker Compose configuration for running the application.

- `k8s-deployment.yaml`: Kubernetes deployment configuration for the application.

- `pom.xml`: Parent Maven configuration file for the entire project.
