# See here for image contents: https://github.com/microsoft/vscode-dev-containers/tree/v0.245.0/containers/java/.devcontainer/base.Dockerfile
# [Choice] Java version (use -bullseye variants on local arm64/Apple Silicon): 11, 17, 11-bullseye, 17-bullseye, 11-buster, 17-buster
ARG BASE_IMAGE_VERSION_CODENAME=bookworm
FROM mcr.microsoft.com/devcontainers/base:${BASE_IMAGE_VERSION_CODENAME}
USER root
ENV JAVA_HOME=/usr/lib/jvm/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

ARG INSTALL_ZSH="false"
# [Option] Upgrade OS packages to their latest versions
ARG UPGRADE_PACKAGES="false"
# Install needed packages and setup non-root user. Use a separate RUN statement to add your own dependencies.
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# Install needed packages and setup non-root user. Use a separate RUN statement to add your own dependencies.
COPY library-scripts/*.sh library-scripts/*.env /tmp/library-scripts/
# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
RUN export DEBIAN_FRONTEND=noninteractive \
    && /bin/bash /tmp/library-scripts/common-debian.sh "${INSTALL_ZSH}" "${USERNAME}" "${USER_UID}" "${USER_GID}" "${UPGRADE_PACKAGES}" "true" "true" \
    #
    # ****************************************************************************
    # * TODO: Add any additional OS packages you want included in the definition *
    # * here. We want to do this before cleanup to keep the "layer" small.       *
    # ****************************************************************************
    # && apt-get -y install --no-install-recommends <your-package-list-here> \
    && apt-get -y install --no-install-recommends curl \
    && apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/* /tmp/library-scripts
# [Optional] Uncomment this section to install additional OS packages.
# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#     && apt-get -y install --no-install-recommends <your-package-list-here>
USER vscode
RUN curl -s "https://get.sdkman.io" | bash
# this SHELL command is needed to allow using source
SHELL ["/bin/bash", "-c"]    
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" \
    && echo sdkman_auto_answer=true > ${SDKMAN_DIR}/etc/config \
    && sdk install java 21.0.2-open \
    && sdk install maven 3.9.5 \
    && sdk install mvnd 1.0.2

USER root