# Plan de presentation - 10 minutes

## Slide 1 - Titre

LeanMass Calculator: durcissement securite Android base sur OWASP MASVS.

## Slide 2 - Presentation de l'application

- Application Kotlin Android.
- XML + ViewBinding.
- Authentification locale.
- Calcul LBM.
- Historique SQLite.
- Profil utilisateur.

## Slide 3 - Analyse initiale

- Donnees locales sensibles.
- Session sans expiration.
- Politique de mot de passe basique.
- Screenshots possibles.
- Pas de statut root/debug.

## Slide 4 - Mapping MASVS

- MASVS-STORAGE: SQLite, SharedPreferences, backup.
- MASVS-AUTH: mot de passe et session.
- MASVS-CRYPTO: PBKDF2 et sel.
- MASVS-PLATFORM / PRIVACY: FLAG_SECURE.
- MASVS-RESILIENCE: root/debug awareness.

## Slide 5 - Durcissement authentification

- Validation email.
- Mot de passe fort.
- Confirmation mot de passe.
- Message login generique.
- Tests unitaires.

## Slide 6 - Stockage local et session

- Backup Android desactive.
- Exclusion database/sharedpref.
- Session minimale: userId + timestamp.
- Expiration apres 24 heures.
- Logout efface la session.

## Slide 7 - Protection vie privee et plateforme

- FLAG_SECURE sur ecrans sensibles.
- Protection contre screenshot/screen recording.
- Statut securite dans Profile.
- Detection non bloquante root/debug/debuggable.

## Slide 8 - Scenario de demonstration

1. Creer un compte avec mot de passe faible: refuse.
2. Creer un compte avec mot de passe fort: accepte.
3. Faire un calcul LBM.
4. Voir l'historique.
5. Ouvrir Profile et lire le statut securite.
6. Se deconnecter et verifier le retour login.

## Slide 9 - Resultats

- Build et tests passent.
- Fonctionnalites originales conservees.
- Plusieurs controles MASVS implementes.
- Application testee sur telephone reel.

## Slide 10 - Conclusion

- Durcissement realiste pour une application locale.
- Protection amelioree des donnees utilisateur.
- Limites: pas de chiffrement SQL complet, root detection simple, pas de backend distant.
- Pistes futures: SQLCipher/EncryptedSharedPreferences, attestation Play Integrity, CI securite.
