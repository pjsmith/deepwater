# Implementation Plan

This plan outlines the steps to build the roguelike game. It is divided into phases, starting with a minimal viable product and iteratively adding features.

## Phase 1: Core Engine Foundation

The goal of this phase is to create a non-interactive engine that can represent the game world.

1.  **Project Setup**: Initialize the `engine` sub-project with a `deps.edn` file.
2.  **Data Modeling**:
    *   Define the core data structures for the game state in `engine/src/deepwater/engine/game_state.clj`. This will include the game map, player, monsters, and items.
    *   The map will be a 2D vector of vectors (or a map of coordinates to tile data).
    *   Tiles will be maps containing keys like `:glyph`, `:walkable?`, `:color`, `:items`, `:monster`.
3.  **Map Generation**:
    *   Create a `engine/src/deepwater/engine/map_generator.clj` namespace.
    *   Implement a simple map generator (e.g., a rectangular room).
    *   Write a function to place the player (`@`) and some monsters (`o`) on the map.
4.  **Initial State**: Create a function that generates a complete initial `game-state` map by composing the map generation and entity placement functions.

## Phase 2: Interactive Engine

This phase introduces interaction via `core.async` channels.

1.  **IO Channels**:
    *   In `engine/src/deepwater/engine/core.clj`, define the input and output `core.async` channels.
    *   Define the data format for input messages (e.g., `{:command :move, :direction :north, :correlation-id "..."}`) and output messages (e.g., `{:type :game-state-update, :state {...}, :correlation-id "..."}`).
2.  **Game Loop**:
    *   Implement the main game loop in `engine/src/deepwater/engine/game_loop.clj`.
    *   This will be a `go` block that continuously reads from the input channel.
3.  **Command Processing**:
    *   The game loop will dispatch commands to handler functions.
    *   Implement a `move` command handler that updates the player's position in the game state.
    *   After processing a command, the loop will put the updated game state onto the output channel.

## Phase 3: Web Server Host

This phase exposes the game engine over HTTP.

1.  **Project Setup**: Initialize the `server` sub-project with `deps.edn` and dependencies for Ring, Reitit, and Muuntaja.
2.  **HTTP Server**:
    *   In `server/src/deepwater/server/core.clj`, create a basic Ring server.
3.  **API Endpoints**:
    *   Create a `deepwater.server.web` namespace for Reitit routes.
    *   **`POST /api/game/command`**: This endpoint will take a command (e.g., `{"command": "move", "direction": "north"}`), put it on the engine's input channel, and wait for a response on the output channel with a matching correlation ID. It will then return the new game state as JSON.
    *   **`GET /api/game/state`**: Returns the current game state.
    *   **`GET /game`**: Serves the static client files from the `client/` directory.
4.  **Engine Integration**: The server will start and hold a reference to the engine's IO channels.

## Phase 4: JavaScript Client

This phase builds the user-facing client application.

1.  **Project Setup**: Initialize the `client` project with a `package.json` (even if it's just for metadata) and create `index.html`, `app.js`, and `style.css`.
2.  **HTML Structure**: `index.html` will contain a `<pre id="game-display"></pre>` element for the ASCII view.
3.  **Client Logic (`app.js`)**:
    *   **`init()`**: On page load, fetch the initial game state from `/api/game/state`.
    *   **`render(state)`**: A function to clear the `<pre>` block and render the map from the game state, using the specified characters and colors.
    *   **Input Handling**: Add a `keydown` event listener.
    *   **Keymap**: Create a map object to translate key codes to game commands (e.g., `'w': {command: 'move', direction: 'north'}`).
    *   **API Communication**: When a mapped key is pressed, send a `POST` request to `/api/game/command`. On success, call `render()` with the response data.

## Phase 5: Gameplay Features

With the full stack in place, this phase adds core gameplay mechanics.

1.  **Combat**:
    *   Implement an `:attack` command.
    *   Add HP to player and monsters.
    *   When the player moves into a monster, it should be interpreted as an attack.
2.  **Monster AI**:
    *   In the game loop, after processing player input, iterate through all monsters.
    *   Implement simple AI (e.g., if the player is within a certain distance, move towards the player).
3.  **Inventory**:
    *   Implement `:pickup` and `:drop` commands.
    *   Add an `inventory` key to the player state.
    *   Display the player's inventory on the client.

## Phase 6: Tooling and Productionizing

This phase prepares the project for deployment and development.

1.  **Map Tester Tool**:
    *   Create the `tools` sub-project.
    *   Implement a simple command-line application in `tools/src/deepwater/tools/core.clj` that uses the `map_generator.clj` namespace to generate and print a map to the console.
2.  **Configuration**:
    *   Load keybindings and display settings from EDN files and expose them to the client.
3.  **Containerization**:
    *   Write the `Dockerfile` for building and running the server in production.
    *   Write the `docker-compose.yml` to run the application.
4.  **Documentation**:
    *   Update `README.md` with build and run instructions.
    *   Flesh out `docs/Design.md` with decisions made during implementation.
