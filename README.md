Add this to the `config` container in `docker-compose.yml` if you want to connect to a local vitamax-config repository:
```yaml
volumes:
  - ${VITAMAX_CONFIG_PATH}:${VITAMAX_CONFIG_PATH}
```