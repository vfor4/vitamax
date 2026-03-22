Add this to the `config` container in `docker-compose.yml` if you want to connect profile `dev` to a local vitamax-config repository:
1. `docker-compose.yml`
    ```yaml
    environment:
      - VITAMAX_CONFIG_PATH=${VITAMAX_CONFIG_PATH}
    volumes:
      - ${VITAMAX_CONFIG_PATH}:${VITAMAX_CONFIG_PATH}
    ```
2. ignore the connect to config repo in `config-service/application-dev.yml`:
2.1. add this if your local config repo is not in `main` branch: 

        `spring.cloud.config.server.git.default-label: <branch-name>`