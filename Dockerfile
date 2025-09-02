# Stage 1: This step will make sure the image has not been tampered with and is legitimate. (optional)
FROM ghcr.io/sigstore/cosign/cosign:v2.4.1 AS cosign-bin
FROM alpine:3.22 AS verifier
COPY --from=cosign-bin /ko-app/cosign /usr/local/bin/cosign
RUN cosign verify gcr.io/distroless/java21-debian12:nonroot \
    --certificate-oidc-issuer https://accounts.google.com \
    --certificate-identity keyless@distroless.iam.gserviceaccount.com \
    && echo "VERIFIED" > /tmp/trigger \
    || (echo "VERIFICATION FAILED" > /tmp/trigger && exit 1)

# Stage 2: This is the image that will be created, will be using a distroless base image.
FROM gcr.io/distroless/java21-debian12:nonroot AS runner
WORKDIR /app
# This step is to force Stage 2 to run, nothing is actually copied to this image/container.
COPY --from=verifier /tmp/trigger /dev/null
COPY ./build/libs/user-management-api-0.0.1-SNAPSHOT.jar ./user-management-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "user-management-api.jar"]


