terraform {
  required_version = ">= 1.3.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 3.43.0"
    }
  }
  cloud {
    organization = "adrianrogalski"
    workspaces {
      name = "TerraformCI"
    }
  }
}

provider "azurerm" {
  features {}
}

# --- ZMIENNE ---

variable "location" {
  type    = string
  default = "westeurope"
}

variable "resource_group_name" {
  type    = string
  default = "rg-cardu-readoo"
}

variable "app_image" {
  description = "Obraz Dockera aplikacji (z GHCR)"
  type        = string
  # PODMIEŃ NA SWÓJ
  default = "ghcr.io/adrianrogalski/cardu-readoo:latest"
}

variable "mssql_url" {
  description = "URL dla SQL"
  type        = string
  sensitive   = true
}

variable "mssql_sa_password" {
  description = "Hasło SA dla SQL"
  type        = string
  sensitive   = true
}

variable "setup_token" {
  description = "SETUP_TOKEN dla aplikacji"
  type        = string
  sensitive   = true
}

# --- RESOURCE GROUP ---

resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location
}

# --- LOG ANALYTICS ---

resource "azurerm_log_analytics_workspace" "logs" {
  name                = "log-${var.resource_group_name}"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# --- CONTAINER APPS ENV ---

resource "azurerm_container_app_environment" "env" {
  name                       = "cae-cardu-readoo"
  location                   = azurerm_resource_group.rg.location
  resource_group_name        = azurerm_resource_group.rg.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.logs.id
}

# --- CONTAINER APP: app + sqledge ---

resource "azurerm_container_app" "cardu" {
  name                         = "cardu-readoo-app"
  resource_group_name          = azurerm_resource_group.rg.name
  container_app_environment_id = azurerm_container_app_environment.env.id

  revision_mode = "Single"

  ingress {
    external_enabled = true
    target_port      = 8080

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  # Sekrety
  secret {
    name  = "db-url"
    value = var.mssql_url
  }
  # Sekrety
  secret {
    name  = "db-password"
    value = var.mssql_sa_password
  }

  secret {
    name  = "setup-token"
    value = var.setup_token
  }

  template {
    # --- KONTENER APLIKACJI ---
    container {
      name   = "app"
      image  = var.app_image
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "DB_URL"
        value = "db-url"
      }

      env {
        name  = "DB_USERNAME"
        value = "sa"
      }

      env {
        name        = "DB_PASSWORD"
        secret_name = "db-password"
      }

      env {
        name  = "SPRING_JPA_DATABASE_PLATFORM"
        value = "org.hibernate.dialect.SQLServerDialect"
      }

      env {
        name        = "SETUP_TOKEN"
        secret_name = "setup-token"
      }
    }
  }
}