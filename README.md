[![Build Status](https://travis-ci.org/k0shk0sh/FastHub.svg?branch=master)](https://travis-ci.org/k0shk0sh/FastHub) [![Build status](https://ci.appveyor.com/api/projects/status/2yhxx7hu6hju24bk?svg=true)](https://ci.appveyor.com/project/k0shk0sh/fasthub)
[![Releases](https://img.shields.io/github/release/k0shk0sh/FastHub.svg)](https://github.com/k0shk0sh/FastHub/releases/latest) [![Slack](https://img.shields.io/badge/slack-join-e01563.svg)](http://rebrand.ly/fasthub) [![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=kosh20111@gmail.com)

![Logo](/.github/assets/feature_graphic.png?raw=true "Logo")

# FastHub  
Yet another **open-source** GitHub client app but unlike any other app, FastHub was built from scratch.  
<!--
[<img src="https://f-droid.org/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="80">](https://f-droid.org/repository/browse/?fdid=com.fastaccess.github)
-->
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" 
      alt="Download from Google Play" 
      height="80">](https://play.google.com/store/apps/details?id=com.fastaccess.github)
[<img src=".github/assets/direct-apk-download.png" 
      alt="Direct apk download" 
      height="80">](https://github.com/k0shk0sh/FastHub/releases/latest)

#### Snapshots / Test builds

We have configurated snapshots of FastHub, which can be downloaded from [AppVeyor CI](https://ci.appveyor.com/project/k0shk0sh/fasthub/build/artifacts).

# Features  
- **App**
  - Two login types (Basic Auth), (Access Token) or via (Browser)
  - Themes mode
  - Offline-mode
  - Markdown and code highlighting support
  - Notifications overview and "Mark all as read"
  - Search users/orgs, repos, issues/prs & code.
  - Pinned Repos
  - Trending
- **Repositories**
  - Browse & Read Wiki
  - Search Repos
  - Browse and search Repos
  - See your public, private and forked Repos
  - Filter Branches and Commits
  - Watch, star and fork Repos
  - Download releases, files and branches
- **Issues and Pull Requests** 
  - Search Issues/PRs
  - Filter Issues/PRs
  - Long click to peak Issues/PRs & add comments otg.
  - Open/close Issues/PRs
  - Comment on Issues/PRs
  - Manage Issue/PR comments
  - React to comments with reactions
  - Edit Issues/PRs
  - Lock/unlock conversation in Issues/PRs
  - Assign people and add Labels and Milestones to Issues/PRs
  - Manage Milestones
  - Merge PRs
  - PRs reviews(reply, react with emojies, delete & edit comment)
  - PRs statuses
- **Commits and Gists**
  - Search Code/Gists
  - View Gists and their files
  - Comment on Commits/Gists
  - Manage Commit/Gist comments
  - Create/Delete Gists
  - React to Commit comments with reactions
- **Orgs**
    - Overview
    - Feeds
    - Teams & Teams repos
    - Repos
- **Users**
  - Follow/unfollow users
  - View user feeds
  - Contribution graph.
  - Search Users, Repos, Issues,Pull Requests and Code
- _**Much more...**_
  - _FastHub is actively developed. More features will come!_

~~> **FastHub** contain Ads, which are disabled by default. You could enable them if you'd like to support the development.~~  
_Ads currently not available._

# Specs / Open-source libraries:

- Minimum **SDK 21**, _but AppCompat is used all the way ;-)_
- **Kotlin** all new modules starting from 2.5.3 will be written in **#Kotlin**.
- **MVP**-architecture: [**ThirtyInch**](https://github.com/grandcentrix/ThirtyInch) because its ThirtyInch.
- [**RxJava2**](https://github.com/ReactiveX/RxJava) & [**RxAndroid**](https://github.com/ReactiveX/RxAndroid) for Retrofit & background threads
- [**Retrofit**](https://github.com/square/retrofit) for constructing the REST API
- [**Requery**](https://github.com/requery/requery/) for offline-mode
- [**Stream API**](https://github.com/aNNiMON/Lightweight-Stream-API) for dealing with `Collections`
- [**ButterKnife**](https://github.com/JakeWharton/butterknife) for view binding
- [**Android State**](https://github.com/evernote/android-state) for saving instance states
- [**Lombok**](https://projectlombok.github.io) for getters and setters
- [**Material-BottomNavigation**](https://github.com/sephiroth74/Material-BottomNavigation) for `BottomBar` tabs 
- [**Android-Universal-Image-Loader**](https://github.com/nostra13/Android-Universal-Image-Loader) for loading images
- [**commonmark**](https://github.com/atlassian/commonmark-java) for _Markdown_ conversion to html
- [**Toasty**](https://github.com/GrenderG/Toasty) for displaying error/success messages
- [**ShapedImageView**](https://github.com/gavinliu/ShapedImageView) for round avatars
- [**MaterialTapTargetPrompt**](https://github.com/sjwall/MaterialTapTargetPrompt) for displaying tips throughout the app
- [**Material-About-Library**](https://github.com/daniel-stoneuk/material-about-library) for the about screen
- **Fabric** analytics & crash reporting.
- **Android Support Libraries**, the almighty ;-)

# Contribution

You love FastHub? You want new features or bug fixes?  
Please **contribute** to the  project either by [_creating a PR_](https://github.com/k0shk0sh/FastHub/compare) or [_submitting an issue_](https://github.com/k0shk0sh/FastHub/issues/new) on GitHub.  
Read the [**contribution guide**](.github/CONTRIBUTING.md) for more detailed information.

### Language Contributors

- Chinese (Simplified) [@Devifish](https://github.com/Devifish)
- Chinese (Traditional) [@maple3142](https://github.com/maple3142)
- German [@failex234](https://github.com/failex234)
- Indonesian [@dikiaap](https://github.com/dikiaap)
- Italian [@Raffaele74](https://github.com/Raffaele74)
- Japanese [@Rintan](https://github.com/Rintan)
- Lithuanian [@mistermantas](https://github.com/mistermantas)
- Russian [@dedepete](https://github.com/dedepete)
- Turkish [@kutsan](https://github.com/kutsan)
- Portuguese [@caiorrs](https://github.com/caiorrs)

# License

> Copyright (C) 2017 Kosh.  
> Licensed under the [GPL-3.0](https://www.gnu.org/licenses/gpl.html) license.  
> (See the [LICENSE](https://github.com/k0shk0sh/FastHub/blob/master/LICENSE) file for the whole license text.)

# Screenshots

| Mobile Light | Mobile Dark |
|:-:|:-:|
| ![Main Screen Light](/.github/assets/mobile_light.png?raw=true) | ![Main Screen Dark](/.github/assets/mobile_night.png?raw=true) |

| Tablet Light | Tablet Dark |
|:-:|:-:|
| ![Main Screen Light](/.github/assets/tablet_light.png?raw=true) | ![Main Screen Dark](/.github/assets/tablet_night.png?raw=true) |

# FastHub Logo

**FastHub** logo is designed by **Kevin Aguilar**.  
[Google+](https://plus.google.com/+KevinAguilarC) | [Twitter](https://twitter.com/kevttob)  
Designer at [221 Pixels](https://www.221pixels.com/).  
Laus Deo Semper
