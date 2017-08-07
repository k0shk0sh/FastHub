# How to submit Issue/Feature Request to *FastHub*

- Make sure the included template is filled ( using FastHub will fill them up automatically ). 
- If you planning to report multiple FRs/Issues that falls under the same feature __PLEASE REPORT THEM IN ONE TICKET__.
- Make sure you are always on latest (FastHub/About & Click on version name).
- Make sure the issue doesn't exists, lets keep things clean & tidy here :).  


# How to contribute & build *FastHub*

If you have a question in mind, feel free to come our public [Slack](http://rebrand.ly/fasthub-slack) channel.

### Optional

- Please update debug_gradle.properties file and change below if you like to use your own keys otherwise keep them as they're debug keys:
    - github_client_id= your  github clientId
    - github_secret= your github secret
    - redirect_url= (redirect_url must match the url defined in `AndroidManifest` under `LoginView`)

### Before you import the project to Android Studio:

- Make sure you have Android Studio 3.0 C4 & above.
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

1. First fork the repository by clicking **Fork** button.
1. Clone your own forked repository to your computer.
1. Create and switch branch by typing `git checkout -b <language>` where `<language>` is the language you want to translate to.
1. Create a folder named `values-<language-code>`, where `<language-code>` is your 2 letter code for your language. For example `values-es` for Spanish, `values-fr` for French.
1. Copy `values/strings.xml` to inside `values-??` folder with `cp values/strings.xml values-??/`.
1. Open `values-??/strings.xml` on your editor of choice.
1. Translate and keep in mind those important points.
	1. Obey XML format. So, `<string name="do-not-change">ONLY TRANSLATE HERE</string>`.
	1. Don't translate Git terms. Such as *pull request, push, commit, branch*...
	1. There are special characters and variables. Such as `\n` for newline, `\t` for tab. Keep them in the same position in your sentences. Do not delete them!
	1. Don't translate lines that contain `translatable="false"` statement.
	1. Don't add extra spaces or periods anywhere. Don't delete current ones. Keep them as is.
1. Once finished the translations, add files to the git index with `git add values-??/strings.xml` and commit it with `git commit -m 'Language: Strings translated'`.
1. Then push your local changes to your forked repository branch by typing `git push origin <language>`.
1. Finally, create a pull request from your branch to our *master* with **Pull Request** button.

# Translators

- **English**: Default
