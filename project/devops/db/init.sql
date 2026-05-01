-- Création de la base de données (si nécessaire)
-- PostgreSQL crée automatiquement la DB depuis POSTGRES_DB

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Insert default admin user (password: Admin@123)
-- Le mot de passe sera encodé par BCrypt au démarrage via DataInitializer
-- Ce script est purement documentaire; Spring Boot gère la création des tables

-- Catégories de base (insérées par DataInitializer.java)
-- Smartphones, Ordinateurs, Tablettes, Accessoires, Audio, TV & Écrans
