#!/bin/bash -e
echo "Configuring dependencies before build"

cp java/registry/src/main/resources/frame.json.sample java/registry/src/main/resources/frame.json
cp java/registry/src/main/resources/audit_frame.json.sample java/registry/src/main/resources/audit_frame.json
rm java/registry/src/main/resources/public/_schemas/*
cp sb_registry/schemas/* java/registry/src/main/resources/public/_schemas/

echo "Copying sample from sb_registry to resources"
cp sb_registry/application.yml.sample java/registry/src/main/resources/application.yml.sample

echo "Copy the sample config as main application config"
cp java/registry/src/main/resources/application.yml.sample java/registry/src/main/resources/application.yml

echo "Configuration of dependencies completed"
