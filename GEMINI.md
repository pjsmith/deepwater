# Environment

E1. You are working on Windows. Use windows file paths (starting with a drive letter, then a colon, e.g. c:/foo/bar) for all file system operations and tool invocations

# Coding standards

## General

G1. All projects must have a README.md file. The README must briefly explain what the project does, then how to build it. 
G2. If any code you write invalidates the instructions in the README, update the README.
G3. Every project must have a Dockerfile. The Dockerfile should be a multi-stage Dockerfile which builds the application in production mode in one stage, and runs it in another stage.
G4. If the application has dependencies (database etc.) the repository should include a docker-compose file which allows a developer to run the application _and_ dependencies all in one.
G5. Every project should have a docs/ directory where more detailed docs about the project live. This includes technical and non-technical docs, design docs, etc.

## Process

P1. The first step in implementation of any project is reading the supplied design or specification, and expanding it into a docs/Spec.md file. Ask questions to clarify important points as you do this.
P2. The next step is planning out a way to implement the project, and emitting that detailed plan into a docs/Plan.md file. The plan should focus on getting a working system running, then iterating on it bit-by-bit. It should go into detail about namespace design and implementation, data flows, data models, etc. Write the plan, then wait for explicit instructions to start implementing.
P3. Any design decisions throughout the planning and implementation of the project should be recorded in detail in docs/Design.md
P4. As you progress, If we make any decisions or take any actions which contradict the plan, update Plan.md.

## Clojure

C0. This project uses Clojure as its language for back-end work.
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
C12. Prefer modeling input and output, and communication between components -- any IO activity or asynchronous activity -- with core.async channels.
C13. Use conventional techniques from the Clojure and golang worlds for core.async communication, e.g. passing a structure containing a channel, then listening for a response on the channel.
C14. Use spec at the edges (IO and interaction between gross components), but do not generally use spec for internals of the system. If there is a good reason to use spec outside these guidelines, outline it and ask for permission

## Clojure testing

T1. Use clojure.test
T2. Use macros more liberally in test code, if it makes test cases clearer and more declarative.


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
    - Input and output should be asynchronous and mediated via core.async channels: one channel for input, one for output. The buffering behaviour of these channels should be configurable by the host of the engine.
    - Inputs should be related to outputs with a GUID correlation identifier
* Tools
    - Extra tools for managing assets
    - Test harnesses for trying out map generation
    - Any other tools that I ask for or that you judge useful
* Server host
    - Hosts the game engine in a web server. Input and output to/from the game loop should be via HTTP request/response. This should be layered over the asynchronous channels of the game engine.
    - Should be able to serve content as JSON or HTML according to HTTP content negotiation
    - Should be able to accept requests as POSTs with JSON or form data
* Client
    - Should be served by the server host at the path /game
    - Should be a simple Javascript application which interacts with the server host. It should accept input from the user as keypresses, and should render the output as ASCII in a `pre` block or similar. 
    - The display should use colour
    - Keybindings should be configurable via an EDN file
    - Display colours and characters should be configurable via an EDN file

## Future state

The game engine should be designed to support different hosts, in particular:
* A networked host where the client and server are on different machines, and the client is an independently-developed third-party client
* A terminal emulator host
* A graphical host

## Game design

Make the display and interaction as close as possible to ADOM, e.g. '#' for walls, 'o' for an orc, '@' for the player, etc.