# Mapping OWASP MASVS - LeanMass Calculator

| Categorie MASVS | Faiblesse initiale | Mesure implementee | Preuve / fichier | Test ou demonstration |
|---|---|---|---|---|
| MASVS-STORAGE | SQLite et SharedPreferences non exclus explicitement des sauvegardes | `allowBackup=false`, exclusions backup/data extraction | `AndroidManifest.xml`, `backup_rules.xml`, `data_extraction_rules.xml` | Inspecter manifest/XML; build reussi |
| MASVS-PRIVACY | Donnees de sante visibles dans Home/History/Profile | `FLAG_SECURE` sur ecrans sensibles | `SecureScreenHelper.kt`, activites UI | Essayer une capture d'ecran; elle est bloquee si le flag est actif |
| MASVS-AUTH | Mot de passe faible possible | Politique forte: longueur, majuscule, minuscule, chiffre, special | `AuthValidator.kt` | Tests `AuthValidatorTest`; register faible refuse |
| MASVS-AUTH | Session sans expiration | Timestamp de login et expiration apres 24h | `SessionManager.kt`, `SessionPolicy.kt` | Tests `SessionPolicyTest`; relance apres expiration redirige vers login |
| MASVS-CRYPTO | Besoin de demontrer le hachage correct | PBKDF2WithHmacSHA256, SecureRandom, Base64, 150000 iterations, 256 bits | `PasswordHasher.kt` | Tests `PasswordHasherTest` |
| MASVS-STORAGE | Historique local potentiellement multi-utilisateur | Requetes filtrees par `userId` | `CalculationDao.kt` | Creer deux comptes; chaque compte voit seulement son historique |
| MASVS-CODE | Risque de logs sensibles | Aucun log de mot de passe/hash/sel; messages generiques | Scan source `Log`, `println`, `Toast` | Recherche code et tests manuels |
| MASVS-RESILIENCE | Pas de signalement root/debug | Detection non bloquante root/debug/debuggable | `SecurityChecks.kt`, `ProfileActivity.kt` | Voir le statut dans Profile |
| MASVS-PLATFORM | Composants exportes implicitement | Activites non launcher `exported=false` | `AndroidManifest.xml` | Inspecter manifest |
| MASVS-PRIVACY | Documentation securite absente dans app | Section securite dans Profile et README | `activity_profile.xml`, `README.md` | Ouvrir Profile et lire README |
