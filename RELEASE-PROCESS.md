# Release process

At this stage, the release process is manual.

## Package the release

Assuming we are on a snapshot version `1.3-SNAPSHOT`

```shell
mvn package
```

## Set new version

Set the new version to `1.4`

```shell
mvn versions:set
```

## Release version

This command will build the jars and release to mvn repository

```shell
mvn install -Prelease
```

## Commit the new pom files

```shell
git add .
git commit -m"upped to v 1.3"
git push
```

## Create a new version tag

```shell
git tag -a v1.3 -m "version 1.3"
git push origin v1.3
```

## Set new version and commit

Assuming new version is 1.4-SNAPSHOT

```shell
mvn versions:set
git add .
git commit -m"upped to version 1.4-SNAPSHOT"
```

