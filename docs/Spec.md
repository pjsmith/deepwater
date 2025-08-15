# Specification

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
