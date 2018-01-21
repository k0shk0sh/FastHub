# Contributing to FastHub

**Updated: 19 Jan 2018**

## Table of contents

1. [Submitting Issues](#submitting-issues)
1. [How to contribute. Importing and assembling](#how-to-contribute-importing-and-assembling)
   1. [Custom keys *(optional)*](#custom-keys-optional)
   1. [Before importing into Android Studio](#before-importing-into-android-studio)
   1. [Before import](#before-importation)
   1. [Importing Android Studio project](#importing-android-studio-project)
   1. [Submitting Pull Request](#submitting-pull-request)
1. [Working with translations](#working-with-translations)
1. [Translations Contributors](#translations-contributors)

## Submitting Issues

- Let's keep everything clean and tidy here :)
  - Make sure that similar Issues are not exist. Reopen an Issue if exists but closed.
  - If things you want to submit are related to each other, submit them in one Issue.
- None of the forked Repositories' Issues will be accepted!
  - Forks are developed separately from the origin Repository.
- Make sure you are running latest version (to check it out withing the FastHub head to About and tap on the section with application's version).
- Make sure the included template is filled in (submitting an Issue within FastHub will do it automatically).

## How to contribute. Importing and assembling

If you have any questions, feel free to join our public [Slack](http://rebrand.ly/fasthub) channel.

### Custom keys *(optional)*

- Please update `debug_gradle.properties` file if you want to use your own keys:
  - `github_client_id=` -- your GitHub clientId;
  - `github_secret=` -- your GitHub secret;
  - `redirect_url=` -- the url defined in `AndroidManifest` under `LoginView`.

### Before importation

- Make sure you are running *Android Studio 3.0 C4* or above;
- Import *Android Studio Settings* (to follow project's code style) from [this file](https://raw.githubusercontent.com/k0shk0sh/FastHub/master/fasthub_as_settings.jar);
- Install *Lombok Plugin* from Android Studio Plugins and enable Annotations Processors in Android Studio Preferences.

### Importing Android Studio project

- Fork the Repository.
- Clone it to your workstation.
- Open the project in Android Studio.
- Compile the project for the first time. Then you can start coding.

### Submitting Pull Request

> Please use meaningful commit messages.

- Create a new Branch with the changes you made.
- Submit your Pull Request with an explanation of what have you done and why.

> I really appreciate your efforts on contributing to this project.

## Working with translations

1. Firstly, you have to fork the repository by clicking the **Fork** button.
1. Clone your own forked repository to your workstation.
1. Create and switch Branch by typing `git checkout -b <new branch>` where `<new branch>` is the name of the Branch you want to work with. We recommend you to name it into the language you want to translate in.
1. Create a new directory named like `values-<language code>`, where `<language code>` is a 2 letter ISO code of the language. For example `values-es` for Spanish, `values-fr` for French.
1. Copy `values/strings.xml` into the directory you have created (`values-??`).
1. Open `values-??/strings.xml` in your editor of choice.
1. Translate and keep in mind these important things.
    1. Obey the XML format. So, `<string name="do-not-change">ONLY TRANSLATE HERE</string>`.
    1. Don't translate lines which contain `translatable="false"`.
    1. Don't translate Git and GitHub terms, such as *Pull Request*, *Push*, *Commit*, *Branch*, etc.
    1. There are some escape sequences used in translations (e.g. `\n` as a line feed (new line), `\t` as a tabulator. Don't delete them!
    *For the full list you can see this [Wiki article](https://en.wikipedia.org/wiki/Control_character#In_ASCII).*
    1. There are some characters which **must be escaped** in translations.

        | `"` | `&quot;` |
        |-----|----------|
        | `'` | `&apos;` |
        | `&` | `&amp;` |
        | `>` | `&gt;` |
        | `<` | `&lt;` |
    1. Don't add extra spaces or periods. Don't delete existent ones.
1. Once you finished translating, add new files to the Git index using `git add values-??/strings.xml` command and commit the changes using `git commit -m '<commit message>'`, where `<commit message>` is a short description of changes you made.
1. Push your local changes into your forked repository by typing `git push origin <new branch>`.
1. Finally, create a Pull Request from your Branch to our main Branch *development*.

## Translations Contributors

- *See [README.md](https://github.com/k0shk0sh/FastHub#language-contributors)*
