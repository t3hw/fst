# In Memory Filesystem 

## Functionalities, Features, and Time Complexity for the project implementation:

### AVL Tree Overview

An AVL Tree is a self-balancing binary search tree where the difference in heights (balance factor) of the left and right subtrees of any node is at most 1. This ensures that all major operations—insertion, deletion, and search—are performed in **O(log(n))** time, as the tree remains balanced.

#### Major Operations in O(log(n)):

- **Search**:
    - The AVL tree maintains the binary search tree property, where all nodes in the left subtree are smaller than the root, and all nodes in the right subtree are larger.
    - This allows efficient traversal down the tree, halving the search space at each step, resulting in **O(log(n))** complexity.

- **Insertion**:
    - When a new node is inserted, the AVL tree may become unbalanced. To restore balance, rotations (single or double) are performed.
    - These rotations (e.g., left, right, left-right, or right-left) are **O(1)** operations, and since the height of the tree is **O(log(n))**, the overall insertion remains **O(log(n))**.

- **Deletion**:
    - Similar to insertion, deletion may unbalance the tree. After removing a node, rotations are applied to rebalance the tree.
    - The rebalancing process ensures that deletion also operates in **O(log(n))** time.

#### Wrapping with Java's Built-in Abstractions

By wrapping the AVL tree with Java's built-in abstractions for a `HashMap`, the tree becomes easier to use and integrate into existing Java projects. For example:

- The AVL tree can serve as the underlying data structure for the map, ensuring that keys are stored in sorted order while maintaining efficient lookups and updates.
- This approach provides a familiar interface for developers while leveraging the AVL tree's performance benefits.

#### Minimizing Recursion

Typically, AVL tree operations like insertion and deletion are implemented recursively. However, recursion can lead to higher memory usage due to stack frames, especially for deep trees. By minimizing recursion:

- I have reduced the memory footprint, making the implementation more efficient.
- Iterative approaches are used instead, which rely on explicit stacks or loops to traverse the tree.
- This design choice is particularly beneficial for applications requiring high performance and low memory overhead.

## Core functionalities:


### File System Functionalities

#### Function Prototypes and Descriptions

- **`addFile(string parentDirName, string fileName, integer fileSize)`**  
    Adds a new file under the specified directory.  
    **Time Complexity**: O(log(n)) for directory lookup + O(1) for insertion.

- **`addDir(string parentDirName, string dirName)`**  
    Adds a new directory under the specified parent directory.  
    **Time Complexity**: O(log(n)) for directory lookup + O(1) for insertion.

- **`getFileSize(string fileName)`**  
    Returns the size of the specified file.  
    **Time Complexity**: O(log(n)) for file lookup.

- **`getBiggestFile()`**  
    Returns the name of the file with the maximum size.  
    **Time Complexity**: O(log(n)), as it is saved in a separate HashMap/AVL tree composite for indexing purposes. Alternatively, it also allows to filter files by their size, or query them by a range of sizes. A stack could also be used for O(1) complexity inserts and deletes, but it only allows for checking the maximum file size at any given point in time.

- **`showFileSystem()`**  
    Displays all files and directories in a hierarchical structure, including their properties.  
    **Time Complexity**: O(n) for traversal.

- **`delete(string name)`**  
    Deletes the specified file or directory.  
    **Time Complexity**: O(log(n)) for lookup + O(log(n)) for rebalancing.



## Build and run

With Docker
```bash
./docker-build.sh
docker compose up
```

##  Testing
Navigate to [http://localhost:4100/swagger-ui/index.html/](http://localhost:4100/swagger-ui/index.html/) to test out the endpoints.

## Local development

1. Open the project folder in VS Code:

2. Install the [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) extension for Visual Studio Code.


3. Reopen the folder in a Dev Container:
    - Press `F1` to open the Command Palette.
    - Type and select `Remote-Containers: Reopen in Container`.

4. Wait for the container to build and start. Once ready, the project will open inside the Dev Container environment.

5. All of the required dependencies will be installed automatically.

6. You can use `mvnd` instead of `mvn` for quicker builds.


## Running and debugging

Debugging Options:

This code can be debugged in three different ways:


## Modular Architecture

This repository is designed to serve as a base for extending the project into a modular microservices architecture. The current structure demonstrates how to manage multiple submodules within a single repository while sharing common code and configurations.

### Example Structure for Multiple Microservices

- `common/`: Shared libraries and utilities, can be exported to an external repo manager of some sort. Holds the AVL Tree implementation, as well as its hashmap abstraction layer.

- `filesystem/`: The core of the api, uses the AVL Tree.

- `openapi-spec/`: Holds the swagger schema and configuration

- `db` - unused for this project.

By following this modular approach, the project can scale efficiently while maintaining a clean and organized codebase.


### OpenAPI Generator in the Core Module

The `openapi-spec/` module leverages the OpenAPI Generator to streamline the creation of client libraries, server stubs, and API documentation. This tool ensures consistency between the API specification and the generated code, reducing manual effort and potential errors.

#### Usage

1. **OpenAPI Specification**:
    - The OpenAPI specification file (`model.yaml`) is located in the `openapi-spec/` module, within the `resources/swagger` folder.
    - This file defines the API endpoints, request/response models, and other relevant details.

2. **Generating Code**:
    - Use the OpenAPI Generator Maven plugin configured in the `pom.xml` of the `core/` module to 
    - Running `mvn compile` generates the DTO and Server/Client classes
    - The generated code will be placed in the `target/generated-sources` directory within the `core/` module.

3. **Integration**:
    - The generated code is integrated into the `openapi-spec/` module and shared across other modules, ensuring a single source of truth for API definitions.

By using the OpenAPI Generator, the project maintains a robust and scalable approach to API development.

## Repo structure

Additional directories and files:

- `.devcontainer/`: Contains configuration files for the development container, including Dockerfiles, scripts, and environment variables.
    - `devcontainer.json`: Defines the development container configuration.
    - `docker-compose.yml`: Docker Compose configuration for the development environment.

- `.vscode/`: Contains IDE-specific configuration files.
    - `launch.json`: Debug configurations for the project.


- `docker-bake.hcl`: Configuration file for Docker Buildx to manage multi-target builds.

- `docker-compose.yaml`: Docker Compose configuration for running the application.

- `pom.xml`: Parent Maven configuration file for the entire project.
