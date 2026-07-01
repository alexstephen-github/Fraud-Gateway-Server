# Payment Gateway — Fraud Detection & Risk API (Mock Server)

A Spring Boot mock server implementing every operation in
[`payment-gateway-fraud-detection-risk-api-openapi.yaml`](payment-gateway-fraud-detection-risk-api-openapi.yaml).
It returns realistic, in-memory mock data so clients can be developed and demoed
against a live server, and exposes an interactive **Swagger UI**.

## Tech stack

- Java 21, Spring Boot 3.4
- Spring Web + Bean Validation
- springdoc-openapi (Swagger UI)
- Spring Boot Actuator (health probes)
- Lombok

## Implemented endpoints

| Method | Path | Operation |
|--------|------|-----------|
| POST | `/fraud/score` | getFraudScore |
| POST | `/fraud/review` | triggerFraudReview |
| GET  | `/fraud/review` | listFraudReviews |
| GET  | `/fraud/review/{reviewId}` | getFraudReview |
| POST | `/fraud/review/{reviewId}/approve` | approveFraudReview |
| POST | `/fraud/review/{reviewId}/reject` | rejectFraudReview |
| POST | `/fraud/rules` | createFraudRule |
| GET  | `/fraud/rules` | listFraudRules |
| GET  | `/fraud/rules/{ruleId}` | getFraudRule |
| PUT  | `/fraud/rules/{ruleId}` | updateFraudRule |
| DELETE | `/fraud/rules/{ruleId}` | deleteFraudRule |

Data is held in memory and reset on restart. Two seed fraud reviews and one seed
fraud rule are created at startup.

## Run locally

```bash
mvn spring-boot:run
# or
mvn -DskipTests package
java -jar target/fraud-gateway-server.jar
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health

### Example

```bash
curl -X POST http://localhost:8080/fraud/score \
  -H 'Content-Type: application/json' \
  -d '{"payment_method":"pm_1A2B3C","amount":5000,"currency":"USD","customer_ip":"203.0.113.42"}'
```

## Container image

The [`Dockerfile`](Dockerfile) is a multi-stage build on Red Hat UBI OpenJDK 21
images (non-root, tolerant of OpenShift's arbitrary UID).

```bash
podman build -t fraud-gateway-server:latest .
podman run -p 8080:8080 fraud-gateway-server:latest
```

## Deploy to OpenShift

Two supported layouts:

- **Root [`devfile.yaml`](devfile.yaml) + [`deploy.yaml`](deploy.yaml)** — used by the
  Web Console **Import from Git** (devfile) flow.
- **Per-resource manifests in [`openshift/`](openshift/)** — used by `oc apply` /
  **Import YAML** and the in-cluster BuildConfig flow.

Replace `PROJECT` with your namespace in `devfile.yaml` (`CONTAINER_IMAGE`),
`deploy.yaml`, and `openshift/deployment.yaml`.

### Option A — Import from Git (Web Console, devfile-based) ✅ recommended

1. **+Add → Import from Git**, enter your repo URL.
2. Under **Import Strategy**, pick **Devfile** (auto-detected from the root
   `devfile.yaml`). It builds the image from the `Dockerfile` and applies
   `deploy.yaml` (Deployment + Service + Route).
3. Create, wait for the build + rollout, then open the Route and append
   `/swagger-ui.html`.

> **If you saw:** `Could not fetch kubernetes resource "/deploy.yaml" for
> component "kubernetes-deploy" from Git repository` — that means the devfile
> deploy step couldn't find `deploy.yaml` at the repo root. It now exists in
> this repo; **commit and push `devfile.yaml` and `deploy.yaml`**, then re-run
> the import. (Alternatively, switch **Import Strategy** to **Dockerfile**,
> which ignores the devfile entirely — see Option B.)

### Option B — Import from Git with the Dockerfile strategy (no devfile)

In **Import from Git**, set **Import Strategy → Dockerfile** (path `Dockerfile`).
OpenShift builds the image and generates the Deployment/Service/Route for you —
no `deploy.yaml`/`devfile.yaml` needed.

### Option C — Build in-cluster from the Dockerfile via Import YAML

1. In the OpenShift Web Console, select your project.
2. **+Add → Import YAML** and paste the contents of, in order:
   - `openshift/imagestream-buildconfig.yaml`
   - `openshift/deployment.yaml`
   - `openshift/service.yaml`
   - `openshift/route.yaml`
3. Start a build: **Builds → fraud-gateway-server → Start Build** (or
   `oc start-build fraud-gateway-server`). The BuildConfig uses the Docker
   strategy against the repo `Dockerfile`; on success it pushes to the
   ImageStream, and the Deployment's image trigger rolls out automatically.
4. Open the Route URL and append `/swagger-ui.html`.

### Option D — CLI, from local source (binary build)

```bash
oc new-project fraud-demo
oc apply -f openshift/imagestream-buildconfig.yaml
oc start-build fraud-gateway-server --from-dir=. --follow   # binary build from cwd
oc apply -f openshift/deployment.yaml
oc apply -f openshift/service.yaml
oc apply -f openshift/route.yaml
oc get route fraud-gateway-server -o jsonpath='{.spec.host}{"\n"}'
```

> For the binary build, remove the `source.git` block from the BuildConfig or
> it will be ignored in favor of the uploaded directory.

### Option E — Push a pre-built image

Build and push to your registry, set that reference as the container `image` in
`deployment.yaml`, remove the image-trigger annotation, then apply
`deployment.yaml`, `service.yaml`, and `route.yaml`.

## Notes

- Auth: the OpenAPI declares Basic/Bearer security. This mock **does not enforce
  authentication** — the schemes are documented in Swagger UI for reference only.
- JSON uses `snake_case` (matching the spec), except `errorCode` and
  `validationErrors` in error responses, which are camelCase per the spec.
