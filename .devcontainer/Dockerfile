FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

# The base image may contain a Yarn APT source whose signing key is not
# available anymore. It is not required by Chroniqor, so remove only those
# optional source files before Dev Container features run apt-get update.
RUN rm -f \
    /etc/apt/sources.list.d/yarn.list \
    /etc/apt/sources.list.d/yarn.sources \
    /etc/apt/sources.list.d/yarn.list.save
