# IOTICS4J

A multimodule Java implementation of client libraries for IOTICSpace

## Build

## Update to latest version

After this repository has been locally cloned pull the latest API from IOTICS: 

`git pull --recurse-submodules`

## Protobuffers and gRPC services in iotics-agent-core

Once the protobuf files have been checked out and added as git submodule in `iotics-agent-core/iotics-api` 
they should be symlinked from `src/main/proto`.
If symlink isn't restored, cd into `./iotics-agent-core` and run:
- mkdir `src\main\proto`
- On Windows: `mklink /J src\main\proto\iotics iotics-api\proto\iotics`
- On Linux `ln -s src/main/proto/iotics iotics-api/proto/iotics`

* `mvn package`

## Release notes

* 1.3: fix Publisher API and minor tweaks
* 1.2: fix Searcher API. Correctly use the search timeout
* 1.1: fix on hostAllowList values
* 1.0: initial release