# Plan de test manuel - LeanMass Calculator securise

## Authentification

- [ ] Register avec mot de passe faible `password` -> refuse.
- [ ] Register sans majuscule -> refuse.
- [ ] Register sans minuscule -> refuse.
- [ ] Register sans chiffre -> refuse.
- [ ] Register sans caractere special -> refuse.
- [ ] Register avec `StrongPass1!` -> accepte.
- [ ] Login avec mauvais mot de passe -> message generique `Invalid email or password.`
- [ ] Login avec bon mot de passe -> acces Home.

## Session

- [ ] Logout -> retour Login.
- [ ] Relancer apres logout -> Login.
- [ ] Verifier que SharedPreferences ne stocke ni mot de passe, ni hash, ni sel.
- [ ] Session expiration: modifier/attendre le timestamp pour confirmer la redirection Login.

## Calcul et historique

- [ ] Saisir poids/taille/genre -> calcul LBM affiche.
- [ ] Resultat sauvegarde automatiquement.
- [ ] History affiche LBM, genre, poids, taille, statut, date.
- [ ] Delete un item -> item supprime.
- [ ] Clear history -> confirmation puis suppression.
- [ ] Creer deux comptes -> chaque compte voit seulement son propre historique.

## Vie privee / plateforme

- [ ] Login/Register/Home/History/Profile: tenter screenshot -> bloque si `ENABLE_SECURE_SCREEN=true`.
- [ ] Profile affiche `Security status`.
- [ ] En debug build, Profile peut indiquer environnement debug detecte.

## Stockage / backup

- [ ] Verifier `android:allowBackup="false"` dans manifest.
- [ ] Verifier exclusions `database`, `sharedpref`, `file` dans les regles XML.

## Build et tests

- [ ] `.\gradlew.bat testDebugUnitTest`
- [ ] `.\gradlew.bat build`
- [ ] `.\gradlew.bat assembleDebug`
