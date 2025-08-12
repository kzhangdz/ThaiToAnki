# ThaiToAnki
[![Android][android-badge]][android-url]

ThaiToAnki is a Thai dictionary application for Android that prioritizes multitasking and connectivity with AnkiDroid, to efficiently save words as flashcards.

Anki is a commonly used flashcard application, often used by language learners to aid with memorization. As an open source project, many browser-based applications offer capabilities to directly save flashcards into Anki. However, significantly fewer tools exist to aid mobile users with Anki-cooperative learning. ThaiToAnki was designed to help address the lack of mobile tools for Anki, interfacing with the Android version of Anki, AnkiDroid.

ThaiToAnki offers the following capabilities:
- Dictionary searching, with definitions provided by [thai-language.com][thai-url]
- Flashcard previews, which show all available:
  - Pronunciation
  - Romization
  - Definition
  - Classifiers
  - Word Components
  - Synonyms
  - Related Words
  - Examples
  - Sentences
  - References to thai-language.com and Wiktionary
- Example and sentence selection
- Anki flashcard saving
-  mode, which allows floating windows to overlay over the user's current application

## Usage
### Core Application
<div align="center">
  <video src="https://github.com/user-attachments/assets/92038f6d-d0b7-4057-9dd6-7f8320e9dfb2"/>
</div>

### Multitask Mode
Opening the application from the notifications bar opens a draggable, minimizable floating search bar. Typing in a word will open another window with the resulting flashcards. Pressing the floppy disk icon will save the current flashcard to Anki.

<div align="center">
  <video src="https://github.com/user-attachments/assets/b2bd301b-2674-4b94-8658-7af8c51e9456"/>
</div>

While browsing horizontally, such as when watching a video, opening the keyboard may cover the whole screen. In these cases, the most efficient method to input a search may be to use voice typing.

<div align="center">
  <img width="600" height="270" alt="floating_window_horizontal_keyboard" src="https://github.com/user-attachments/assets/6e0724b9-ef05-461e-8d12-b5ea51d44ed2" />
  <img width="600" height="270" alt="floating_window_horizontal" src="https://github.com/user-attachments/assets/34bbc43d-7b13-41e2-99b8-0596f8aa6fbc" />
</div>

### Anki
Flashcards saved using ThaiToAnki are saved to AnkiDroid in the `ThaiToAnki` deck. Pressing the gray pills will reveal extra details.

<div align="center">
  <img width="270" height="600" alt="anki_flashcard_front" src="https://github.com/user-attachments/assets/e08d58bf-d8ff-43b1-af9d-4227ef4882c0" />
  &nbsp;
  <img width="270" height="600" alt="anki_flashcard_back" src="https://github.com/user-attachments/assets/2165bae2-27e8-4537-afa3-a7b6e81e1528" />
</div>

## Built With
### Language
- [![Kotlin][kotlin-badge]][kotlin-url]

Kotlin is the standard language for Android development. I specifically chose to target Android instead of iOS or cross-platform in order to take advantage of Android's `WindowManager` class, which allows floating window functionality.

### Architecture
- MVVM (Model-View-ViewModel)

Used to ensure proper separation of the data layer and UI layer, based on Android development principles. Also includes data repositories that handle Anki and API data.

### UI
- Jetpack Compose
- XML Layout

The core application was designed using Jetpack Compose, while Multitask Mode was designed using XML Layout. Initially, my intention was to use Jetpack Compose solely, as Android development is trending towards toolkits similar to React Native and Flutter. However, after running into issues displaying Jetpack Compose components in Multitask Mode, I chose to develop XML-based layouts for the floating windows. To reduce the amount of component duplication, I used an AndroidView to inject the flashcard XML into the core application.

### Database
- Room

ThaiToAnki stores each word searched, as well as associated word, into a local Room database. When navigating to a flashcard or looking through search history, view models are used to query the word from the database and display its info. The application uses an `OfflineWordsRepository`, which allows querying words, returned as `Flow` objects from the database access object. Word info is stored as the following tables/entities:

- Word
- Classifier
- Component
- Synonym
- RelatedWord
- Example
- Sentence

### API
- Retrofit

Used to grab word data from [thai-language.com][thai-url]. Required a custom `HtmlConverterFactory` to convert the source HTML to a parsable string. Data stored in `ThaiLanguageRepository`.

### Anki Intents
- com.ichi2.anki.api (AnkiDroid)

Primarily connects to AnkiDroid using `AddContentApi` to add flashcards and `NoteInfo` to retrieve flashcard information.

### Anki Template
- HTML
- CSS (based on a template by [cdmoro](https://github.com/cdmoro/anki-material-flashcard))
- JavaScript

## Data Flow
-- TODO: place a flow chart of key classes here. Separate into layers that will help show off the containers, repositories, ViewModels, etc.

<!-- MARKDOWN LINKS & IMAGES -->
[thai-url]: http://thai-language.com/
[android-badge]: https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white
[android-url]: https://www.android.com/
[kotlin-badge]: https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white
[kotlin-url]: https://kotlinlang.org/




