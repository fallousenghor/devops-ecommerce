# 🛒 Electronics Store — Backend & DevOps

Application e-commerce de produits électroniques — backend Spring Boot avec pipeline CI/CD complet.

---

## 🏗️ Architecture

```
project/
├── backend/                  # API Spring Boot
│   ├── src/main/java/com/ecommerce/
│   │   ├── auth/             # JWT, Security, Filtre
│   │   ├── user/             # Profil utilisateur
│   │   ├── product/          # Produits & catégories
│   │   ├── cart/             # Panier
│   │   ├── order/            # Commandes
│   │   └── common/           # DTO générique, exceptions, DataInitializer
│   └── Dockerfile
├── devops/
│   ├── prometheus/           # prometheus.yml
│   ├── grafana/              # Datasources & dashboards provisionnés
│   ├── db/                   # init.sql PostgreSQL
│   └── jenkins/              # Jenkinsfile
├── docker-compose.yml        # Stack complète
├── Jenkinsfile               # Pipeline CI/CD
├── sonar-project.properties  # Config SonarQube
├── .env.example
└── README.md
```

---

## 🚀 Démarrage rapide

### Prérequis
- Docker & Docker Compose
- Java 17 (pour le dev local)
- Maven 3.9+

### 1. Cloner et configurer
```bash
git clone <votre-repo>
cd project
cp .env.example .env
# Modifier .env si besoin
```

### 2. Lancer toute la stack
```bash
docker-compose up -d
```

### 3. Vérifier que tout tourne
```bash
docker-compose ps
docker-compose logs -f backend
```

---

## 🔗 URLs des services

| Service      | URL                          | Identifiants          |
|-------------|------------------------------|-----------------------|
| API REST     | http://localhost:8080        | —                     |
| Swagger UI   | http://localhost:8080/swagger-ui.html | —            |
| SonarQube    | http://localhost:9000        | admin / admin         |
| Prometheus   | http://localhost:9090        | —                     |
| Grafana      | http://localhost:3000        | admin / admin123      |
| Jenkins      | http://localhost:8090        | (1ère config)         |

---

## 🔐 Comptes de test (créés automatiquement)

| Rôle  | Email                    | Mot de passe |
|-------|--------------------------|--------------|
| ADMIN | admin@electronics.com    | Admin@123    |
| USER  | user@electronics.com     | User@123     |

---

## 📡 API REST — Endpoints

### Authentification (`/api/auth`)
```
POST /api/auth/register   — Inscription
POST /api/auth/login      — Connexion → JWT
```

### Utilisateur (`/api/users`) — JWT requis
```
GET  /api/users/me        — Mon profil
PUT  /api/users/me        — Modifier mon profil
```

### Produits (`/api/products`)
```
GET  /api/products                  — Liste paginée
GET  /api/products/{id}             — Détail
GET  /api/products/search?keyword=  — Recherche avec filtres
POST /api/products          [ADMIN] — Créer
PUT  /api/products/{id}     [ADMIN] — Modifier
DELETE /api/products/{id}   [ADMIN] — Soft delete
```

### Panier (`/api/cart`) — JWT requis
```
GET    /api/cart               — Voir le panier
POST   /api/cart/items         — Ajouter un produit
PUT    /api/cart/items/{id}    — Modifier quantité
DELETE /api/cart/items/{id}    — Supprimer un article
DELETE /api/cart               — Vider le panier
```

### Commandes (`/api/orders`) — JWT requis
```
POST /api/orders                      — Passer une commande
GET  /api/orders                      — Mon historique
GET  /api/orders/{id}                 — Détail d'une commande
GET  /api/orders/admin/all    [ADMIN] — Toutes les commandes
PATCH /api/orders/admin/{id}/status   — Changer le statut
```

---

## 🧪 Tests

```bash
cd backend
mvn test                        # Tests unitaires
mvn test jacoco:report          # Rapport de couverture
# → Rapport : target/site/jacoco/index.html
```

---

## 🐳 Docker — Commandes utiles

```bash
# Rebuild uniquement le backend
docker-compose build backend
docker-compose up -d backend

# Voir les logs
docker-compose logs -f backend
docker-compose logs -f postgres

# Arrêter tout
docker-compose down

# Tout reset (volumes compris)
docker-compose down -v
```

---

## 🔍 Analyse SonarQube

```bash
cd backend
mvn verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<votre_token>
```

---

## ⚙️ Pipeline Jenkins (CI/CD)

**Étapes du pipeline :**
1. **Checkout** — Récupération du code depuis Git
2. **Build** — Compilation Maven
3. **Tests** — Tests unitaires + rapport JaCoCo
4. **SonarQube** — Analyse qualité du code
5. **Package** — Génération du JAR
6. **Docker Build** — Construction de l'image
7. **Deploy** — Déploiement via docker-compose (branche `main` seulement)

**Configuration Jenkins :**
1. Ouvrir http://localhost:8090
2. Installer les plugins : Maven, Docker, JaCoCo, SonarQube Scanner
3. Configurer les credentials : `sonar-token`
4. Créer un Pipeline pointant sur ce repo

---

## 📊 Monitoring Grafana

1. Ouvrir http://localhost:3000 (admin / admin123)
2. La datasource Prometheus est préconfigurée
3. Importer le dashboard Spring Boot JVM (ID Grafana: **4701**)

**Métriques disponibles :**
- `http_server_requests_seconds` — latence HTTP
- `jvm_memory_used_bytes` — usage mémoire JVM
- `hikaricp_connections_active` — pool de connexions DB
- `process_cpu_usage` — CPU

---

## 🌿 Stratégie Git

```
main        ← production stable
develop     ← intégration
feature/*   ← nouvelles fonctionnalités
hotfix/*    ← correctifs urgents
```

---

## 🗺️ Roadmap

- [x] Phase 1 — Backend Spring Boot
- [x] Phase 4 — Docker & docker-compose
- [x] Phase 5 — Pipeline CI/CD Jenkins
- [x] Phase 6 — Monitoring Prometheus + Grafana
- [ ] Phase 2 — Application mobile Flutter
- [ ] Phase 3 — Backoffice Angular
