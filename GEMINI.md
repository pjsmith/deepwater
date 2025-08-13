# Environment

E1. You are working on Windows. Use windows file paths (starting with a drive letter, then a colon, e.g. c:/foo/bar) for all file system operations and tool invocations

# Coding standards

## General

G1. All projects must have a README.md file. The README must briefly explain what the project does, then how to build it. 
G2. If any code you write invalidates the instructions in the README, update the README.
G3. Every project must have a Dockerfile. The Dockerfile should be a multi-stage Dockerfile which builds the application in production mode in one stage, and runs it in another stage.
G4. If the application has dependencies (database etc.) the repository should 

## Clojure

G0. This project uses Clojure as its language for back-end work.
C1. Prefer data to functions, prefer functions to macros.
C2. Do not be afraid to use macros where it makes sense though
C3. Use deps.edn and clojure tools, rather than leiningen
C4. Use well-known and well-understood packages rather than reimplementing functionality 
C5. Design systems in a 'functional core, imperative shell' way. IO and interactions with external systems should be pushed to the outside of the system; 
C6. Use Java interop where necessary. If an element of Java interop is needed in multiple places, wrap it in Clojure in its own namespace.
C7. Use type hints liberally in areas where you use Java interop. 
C8. Namespaces should be narrow but deep.
C9. Every Clojure project should be able to be run with a :dev alias. If run with no alias, it should be built or run in production mode.
C10. Every project should have a dev/ directory containing a tree of Clojure files, which is only included if the project is run in dev/ mode
C11. Configuration should be via EDN files

## Clojure web technologies

W1. Use standard Clojure web technologies, like Ring and Hiccup
W2. Use metosin libraries by preference (reitit, muuntaja, etc.)

# Project

## Overview

This project is a roguelike game.

## Architecture

This project has a client-server architecture, with the central game mechanics and game loop contained in a library.

The components are:

* Game engine library
    - Contains geometry mechanics, NPC and monster logic, map generation, inventory, player-npc interaction and npc-npc interaction, game loop with abstract input/output etc.
* Tools
    - Extra tools for managing assets
    - Test harnesses for trying out map generation
    - Any other tools that I ask for or that you judge useful
* Server host
    - Hosts the game engine in a web server. Input and output to/from the game loop should be via HTTP request/response
    - Should be able to serve content as JSON or HTML according to HTTP content negotiation
    - Should be able to accept requests as POSTs with JSON or form data
* Client
    - Should be served by the server host at the path /game
    - Should be a simple Javascript application which interacts with the server host. It should accept input from the user as keypresses, and should render the output as ASCII in a `pre` block or similar. 
    - The display should use colour
    - Keybindings should be configurable via an EDN file
    - Display colours and characters should be configurable via an EDN file

## Game design

Make the display and interaction as close as possible to ADOM, e.g. '#' for walls, 'o' for an orc, '@' for the player, etc.