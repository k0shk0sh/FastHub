# How to contribute & build *FastHub*

### Registering GitHub Account

Before you close the project, Please read the guide below to be able to login from FastHub.

[GitHub Guide](https://auth0.com/docs/connections/social/github)

- Please updadate debug_gradle.properties file and change below:
    - github_client_id= your  gihub clientId
    - github_secret= your github secret
    - redirect_url= (redirect_url must match the url defined in `AndroidManefist` under `LoginView`)

### Before you import the project to Android Studio:

- Make sure you have Android Studio 2.3 & above.
- Import Android Studio Settings (to ensure same code formatting) from this link [Click here](https://raw.githubusercontent.com/k0shk0sh/FastHub/master/fasthub_as_settings.jar)
- Install Lombok Plugin from Android Studio Plugins & enable Annotations Processors from (Android Studio Preference).

### After above steps:

- Fork the project.
- Clone it to your desktop.
- Open the project from Android Studio.
- Let it build & Start coding.

### Submitting PR

> Please make sure your commit messages are meaningful.
 
- Create new Branch with the feature or fix you made.
- Submit your PR with an explanation of what you did & why (~~if applicable~~).

> I really appreciate your efforts on contributing to this project.

# Contribute Translations

In order to help translations, please take a look https://github.com/k0shk0sh/FastHub-translations repository.
