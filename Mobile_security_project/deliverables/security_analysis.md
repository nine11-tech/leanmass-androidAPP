# Analyse de securite - LeanMass Calculator

## 1. Presentation de l'application

LeanMass Calculator est une application Android Kotlin developpee avec des interfaces XML et ViewBinding. Elle permet a un utilisateur de creer un compte, se connecter, calculer sa masse corporelle maigre selon les formules de Boer, consulter son historique de calculs, supprimer des entrees et se deconnecter.

Les donnees sont stockees localement avec SQLite. Les mots de passe ne sont pas stockes en clair: ils sont proteges avec PBKDF2WithHmacSHA256, un sel aleatoire par utilisateur et un encodage Base64.

## 2. Objectif du mini-projet securite

Le but de ce travail est de reprendre une application mobile existante, d'analyser ses faiblesses potentielles selon OWASP MASVS, puis d'implementer des mesures correctives realistes. Les controles ajoutes restent adaptes a un projet universitaire: ils ameliorent la securite sans transformer l'application en produit bancaire ou medical certifie.

## 3. Analyse de securite initiale

L'application initiale etait fonctionnelle, mais plusieurs points pouvaient etre ameliores:

- Les donnees locales SQLite et SharedPreferences n'etaient pas explicitement exclues des mecanismes de sauvegarde Android.
- La politique de mot de passe verifiait surtout la longueur.
- La session locale ne comportait pas d'expiration.
- Les ecrans contenant des donnees personnelles ou de sante pouvaient etre captures par screenshot.
- L'application ne signalait pas les environnements de debug/root.
- Les regles de protection cryptographique etaient presentes mais pas assez documentees et testees pour un rapport securite.

## 4. Faiblesses identifiees et corrections

### 4.1 Sauvegarde Android et donnees locales

Categorie MASVS: MASVS-STORAGE, MASVS-PRIVACY

Risque: les donnees de compte, session et historique LBM peuvent etre considerees sensibles. Meme si le sandbox Android protege les fichiers de l'application, une mauvaise configuration de sauvegarde peut exposer les bases de donnees ou preferences lors d'une restauration.

Correction:

- `android:allowBackup="false"` dans `AndroidManifest.xml`.
- Exclusion des domaines `database`, `sharedpref` et `file` dans `backup_rules.xml`.
- Exclusion equivalente dans `data_extraction_rules.xml` pour Android 12+.

Fichiers modifies:

- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/xml/backup_rules.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`

### 4.2 Validation forte de l'authentification

Categorie MASVS: MASVS-AUTH

Risque: une politique de mot de passe trop faible facilite les attaques par devinette ou force brute hors ligne si la base locale est extraite.

Correction:

- Minimum 8 caracteres.
- Au moins une majuscule.
- Au moins une minuscule.
- Au moins un chiffre.
- Au moins un caractere special.
- Validation de l'email, du nom complet et de la confirmation de mot de passe.
- Messages d'erreur propres cote interface.
- Message generique en cas d'echec de connexion: `Invalid email or password.`

Fichiers modifies:

- `AuthValidator.kt`
- `AuthRepository.kt`
- `RegisterActivity.kt`
- `LoginActivity.kt`
- `AuthValidatorTest.kt`

### 4.3 Hachage des mots de passe

Categorie MASVS: MASVS-CRYPTO, MASVS-AUTH

Risque: l'utilisation d'un algorithme faible comme MD5, SHA-1 ou SHA-256 simple serait inadaptee pour les mots de passe.

Correction:

- Utilisation de `PBKDF2WithHmacSHA256`.
- Sel aleatoire genere avec `SecureRandom`.
- Sel et hash encodes en Base64.
- 150000 iterations.
- Cle derivee de 256 bits.
- Verification par le meme chemin de hachage.
- Comparaison en temps constant via `MessageDigest.isEqual`.

Fichiers modifies:

- `PasswordHasher.kt`
- `PasswordHasherTest.kt`

### 4.4 Securite de session

Categorie MASVS: MASVS-AUTH, MASVS-STORAGE

Risque: une session locale permanente peut rester active longtemps sur un telephone perdu, partage ou utilise en demonstration.

Correction:

- SharedPreferences ne stocke que `userId` et `login_timestamp`.
- Aucun mot de passe, hash ou sel n'est stocke dans la session.
- Expiration automatique apres 24 heures.
- `SplashActivity` verifie `isSessionValid()`.
- Logout efface completement la session.

Fichiers modifies:

- `SessionManager.kt`
- `SessionPolicy.kt`
- `SecurityConfig.kt`
- `SplashActivity.kt`
- `SessionPolicyTest.kt`

### 4.5 Protection contre les captures d'ecran

Categorie MASVS-PLATFORM, MASVS-PRIVACY

Risque: les ecrans Login, Register, Home, History et Profile peuvent afficher des donnees personnelles ou de sante. Les captures d'ecran peuvent exposer ces informations.

Correction:

- Ajout de `SecureScreenHelper`.
- Activation de `WindowManager.LayoutParams.FLAG_SECURE` sur les ecrans sensibles.
- Controle centralise par `SecurityConfig.ENABLE_SECURE_SCREEN`.

Fichiers modifies:

- `SecureScreenHelper.kt`
- `SecurityConfig.kt`
- `LoginActivity.kt`
- `RegisterActivity.kt`
- `HomeActivity.kt`
- `HistoryActivity.kt`
- `ProfileActivity.kt`

### 4.6 Sensibilisation root/debug/tamper

Categorie MASVS-RESILIENCE, MASVS-CODE

Risque: un appareil root ou une application debuggable facilite l'inspection dynamique, la modification de donnees locales et l'analyse du comportement.

Correction:

- Ajout de `SecurityChecks.kt`.
- Detection non bloquante de chemins `su` courants.
- Detection de `test-keys`.
- Detection du debugger attache.
- Detection du flag debuggable.
- Affichage d'un statut dans l'ecran Profile.

Limite: cette detection est volontairement simple pour un projet academique. Elle n'est pas une protection anti-tamper complete.

Fichiers modifies:

- `SecurityChecks.kt`
- `ProfileActivity.kt`
- `activity_profile.xml`

### 4.7 Acces SQLite et minimisation

Categorie MASVS-STORAGE, MASVS-CODE, MASVS-PRIVACY

Risque: l'historique LBM appartient a un utilisateur. Une requete non filtree pourrait exposer les donnees d'un autre compte local.

Correction:

- Requetes d'historique filtrees par `userId`.
- Suppression d'un element limitee a `id` + `userId`.
- Suppression globale limitee a l'utilisateur connecte.
- Utilisation de requetes parametrees, pas de concatenation SQL avec entree utilisateur.
- Commentaires dans `CalculationDao.kt` pour expliquer le controle.

Fichiers modifies:

- `CalculationDao.kt`
- `CalculationRepository.kt`

## 5. Avant / apres

Avant:

- Application fonctionnelle avec authentification, SQLite et PBKDF2.
- Peu de controles explicites autour de backup, session, screenshots et environnement d'execution.

Apres:

- Application durcie selon plusieurs categories OWASP MASVS.
- Donnees locales exclues des sauvegardes.
- Politique de mot de passe plus forte.
- Session limitee dans le temps.
- Screenshots bloques par defaut sur les ecrans sensibles.
- Statut de securite visible pour la demonstration.
- Tests unitaires ajoutes.

## 6. Conclusion

Cette version de LeanMass Calculator montre une demarche complete de securisation mobile: identification des risques, mapping MASVS, corrections techniques et preuves de verification. Les mesures sont realistes pour un mini-projet universitaire et conservent l'application utilisable sur un telephone Android normal.
