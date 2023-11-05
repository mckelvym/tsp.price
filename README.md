# TSP Price Harvester

Harvests price data from [https://www.tsp.gov/share-price-history/](https://www.tsp.gov/share-price-history/) via [https://api.dailytsp.com/close/](https://api.dailytsp.com/close/)

Notes: [https://mckelvym.gitlab.io/notes/tsp.price](https://mckelvym.gitlab.io/notes/tsp.price)

## Build

In `tsp.price`:

```bash
./gradlew build
```

This will build and push to Docker Hub.

## OpenRewrite

This will run any active recipes. See `build.gradle` file.

```bash
./gradlew rewriteRun
```

To see available recipes:

```bash
./gradlew rewriteDiscover
```

## GitHub

- https://github.com/mckelvym/tsp.price
- [Releases](https://github.com/mckelvym/tsp.price/releases)

## Run

### With Gradle

```bash
./gradlew run --args="--help"
```

### With Docker

```bash
docker run -it registry.hub.docker.com/mckelvym/tsp.price:1.0.0
```

See available options with:

```bash
docker run -it registry.hub.docker.com/mckelvym/tsp.price:1.0.0 --help
```

Example output:

```bash
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.0.5)

2023-04-30T19:43:24.241-04:00  INFO 1 --- [           main] tsp.price.Application                    : Starting Application using Java 17.0.6 with PID 1 (/app/classes started by root in /)
2023-04-30T19:43:24.246-04:00  INFO 1 --- [           main] tsp.price.Application                    : No active profile set, falling back to 1 default profile: "default"
2023-04-30T19:43:24.671-04:00  INFO 1 --- [           main] tsp.price.Application                    : Started Application in 0.663 seconds (process running for 0.938)
Usage: tsp [-hV] [-m=merge-file.csv] [-o=out-file.csv]
  -h, --help      Show this help message and exit.
  -m, --merge-file=merge-file.csv
                  Merge with past entries in this file.
  -o, --out-file=out-file.csv
                  Output to this file, possibly with merges included (if
                    specified)
  -V, --version   Print version information and exit.
```

## Cronjob

```bash
tsp_csv.sh tsp_prices/
```

`tsp_csv.sh`:

```bash
#!/bin/bash

path="$1"

if [ "$path" == "" ] ; then
    echo "Specify path."
    exit 1
fi

cd "$path"
merge_name=tspsharePriceHistory.csv
out_name=tspsharePriceHistory_tmp.csv
archive_name=tspsharePriceHistory`date "+%Y%m%d"`.csv
docker pull registry.hub.docker.com/mckelvym/tsp.price:1.0.0 && docker run --rm --name=tsp-harvester -u $(id -u):$(id -g) -v "$PWD":/data registry.hub.docker.com/mckelvym/tsp.price:1.0.0 --merge-file=/data/$merge_name --out-file=/data/$out_name
if [ -s $out_name ] ; then
    mv $out_name $merge_name
    cp -v $merge_name $archive_name

    git add -u && \
        git commit -m "Daily update $archive_name" && \
        git push

    head $merge_name
else
    echo "WARN! File was zero size!"
fi
exit
```

