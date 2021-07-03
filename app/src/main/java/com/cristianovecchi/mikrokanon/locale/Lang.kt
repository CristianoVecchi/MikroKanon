package com.cristianovecchi.mikrokanon.locale

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.AIMUSIC.ensembleNamesIt
import com.cristianovecchi.mikrokanon.composables.NoteNamesEn
import com.cristianovecchi.mikrokanon.composables.NoteNamesFr
import com.cristianovecchi.mikrokanon.composables.NoteNamesIt
import com.cristianovecchi.mikrokanon.composables.NoteNamesRu

enum class LANGUAGES(val language:String){
    de("Deutsch"), en("English"),es("Español"),
    fr("Français"), it("Italiano"), ru("Русский")
}
val doublingDe = listOf("kleine Sekunde", "große Sekunde", "kleine Terz", "große Terz", "Quarte",
    "übermäßige Quarte", "Quinte", "kleine Sexte", "große Sexte", "kleine Septime", "große Septime",
    "Oktave", "kleine None", "große None", "kleine Dezime", "große Dezime", "Undezime",
    "übermäßige Undezime", "Duodezime", "Tredezime Sexte", "Tredezime Sexte", "kleine Quartdezime", "große Quartdezime", "Doppeloktave")
val doublingEn = listOf("minor 2nd","Major 2nd", "minor 3rd", "Major 3rd", "4th",
    "Augm. 4th", "5th", "minor 6th", "Major 6th", "minor 7th", "Major 7th",
    "Octave", "minor 9th", "Major 9th", "minor 10th", "Major 10th", "11th",
    "Augm. 11th", "12th", "minor 13th", "Major 13th", "minor 14th", "Major 14th", "Double Octave")
val doublingIt = listOf("2a minore","2a Maggiore", "3a minore", "3a Maggiore", "4a",
    "4a Aumentata", "5a", "6a minore", "6a Maggiore", "7a minore", "7a Maggiore",
    "Ottava", "9a minore", "9a Maggiore", "10a minore", "10a Maggiore", "11a",
    "11a Aumentata", "12a", "13a minore", "13a Maggiore", "14a minore", "14a Maggiore", "Ottava doppia")
val doublingFr = listOf("2ème mineur", "2ème Majeur", "3ème mineur", "3ème Majeur", "4e",
    "4e Augmentée", "5e", "6e mineure", "6e Majeure", "7ème mineure", "7ème Majeure",
    "Octave", "9ème mineur", "9ème Majeur", "10ème mineur", "10ème Majeur", "11ème",
    "11ème Augmentée", "12ème", "13ème mineur", "13ème Majeur", "14ème mineur", "14ème Majeur", "Double octave")
val doublingEs = listOf("2a menor", "2a Mayor", "3a menor", "3a Mayor", "4a",
    "4a Aumentada", "5a", "6a menor", "6a Mayor", "7a menor", "7a Mayor",
    "Octava", "9a menor", "9a Mayor", "10a menor", "10a Mayor", "11a",
    "11a Aumentada", "12a", "13a menor", "13a Mayor", "14a menor", "14a Major", "Doble octava")
val doublingRu = listOf("Секунда малая", "Секунда большая", "Терция малая", "Терция большая", "Кварта",
"Кварта увеличенная", "Квинта", "Секста малая", "Секста большая", "Септима малая", "Септима большая",
"Октава", "Нона малая", "Нона большая", "Децима малая", "Децима большая", "Ундецима",
"Ундецима увеличенная", "Дуодецима", "Терцдецима малая", "Терцдецима большая",
    "Квартдецима малая", "Квартдецима большая", "Квинтдецима ")
data class Lang( // English by default
    val noteNames: List<String> = NoteNamesEn.values().map { it.toString() },
    val enterSomeNotes: String = "Enter some notes!",
    val choose2ndSequence: String = "Choose the second sequence!",
    val repeatSequence: String = "Repeat the sequence",
    val OKbutton: String = "OK",
    val selectEnsemble: String = "Select an ensemble!",
    val ensembleNames: List<String> = ensembleNamesEn,
    val selectRhythm: String = "Select a rhythm!",
    val selectDoubling: String = "Select some intervals for doubling!",
    val doublingNames: List<String> = doublingEn,
    val beatsPerMinute: String = "Beats Per Minute",
    val ensemble: String = "Ensemble",
    val bpm: String = "BPM",
    val rhythm: String = "Rhythm",
    val rhythmShuffle: String = "Rhythm shuffle",
    val partsShuffle: String = "Parts shuffle",
    val retrograde: String = "Retrograde",
    val inverse: String = "Inverse",
    val invRetrograde: String = "Inv-Retrograde",
    val doubling: String = "Doubling",
    val spreadWherePossible: String = "Spread where possible",
    val deepSearch: String = "Deep search in four-part canons",
    val exportMidi: String = "Export MIDI",
    val language: String = "Language",
    val credits: String = "Credits"
    ){
    companion object {
        fun provideLanguage(lang: String): Lang {
            return when (lang){
                "de" -> german()
                "en" -> english()
                "es" -> spanish()
                "fr" -> french()
                "it" -> italian()
                "ru" -> russian()
                else -> Lang()
            }
        }
        fun english(): Lang {
            return Lang()
        }
        fun french(): Lang {
            return Lang(
                noteNames = NoteNamesFr.values().map { it.toString() },
                ensemble = "Ensemble ", // let a space because in french [word] [_] [:|!|?] [_]
                bpm = "BPM ",
                enterSomeNotes = "Entrez quelques notes !",
                choose2ndSequence = "Choisissez la deuxième séquence !",
                repeatSequence = "Répéter la séquence",
                selectEnsemble = "Choisissez un ensemble !",
                ensembleNames = ensembleNamesFr,
                beatsPerMinute = "Battements par minute ",
                selectRhythm = "Choisissez un rythme !",
                selectDoubling = "Choisissez des intervalles pour doubler !",
                doublingNames = doublingFr,
                rhythm = "Rythme ", // let a space because in french [word] [_] [:|!|?] [_]
                rhythmShuffle  = "Mélanger le rythme",
                partsShuffle  = "Mélanger les parties",
                retrograde  = "Rétrograde",
                inverse  = "Miroir",
                invRetrograde  = "Miroir du rétrograde",
                doubling  = "Redoublements ", // let a space because in french [word] [_] [:|!|?] [_]
                spreadWherePossible  = "Répandre là où c'est possible",
                deepSearch  = "Recherche poussée dans les canons à quatre voix",
                exportMidi  = "Exporter le fichier MIDI",
                language  = "Langue ",
            )
        }
        fun italian(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                enterSomeNotes = "Digita delle note!",
                choose2ndSequence = "Scegli la seconda sequenza!",
                repeatSequence = "Ripeti la sequenza",
                selectEnsemble = "Scegli un ensemble!",
                ensembleNames = ensembleNamesIt,
                beatsPerMinute = "Pulsazioni al minuto",
                selectRhythm = "Scegli un ritmo!",
                selectDoubling = "Scegli degli intervalli per il raddoppio!",
                doublingNames = doublingIt,
            rhythm = "Ritmo",
             rhythmShuffle  = "Mescola il ritmo",
             partsShuffle  = "Mescola le parti",
             retrograde  = "Retrogrado",
             inverse  = "Inverso",
             invRetrograde  = "Inverso del retrogrado",
             doubling  = "Raddoppi",
             spreadWherePossible  = "Spalma dove è possibile",
             deepSearch  = "Ricerca approfondita nei canoni a quattro parti",
             exportMidi  = "Esporta il file MIDI",
             language  = "Lingua",
            )
        }
        fun spanish(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                enterSomeNotes = "¡Escribe algunas notas!",
                choose2ndSequence = "¡Elige la segunda secuencia!",
                repeatSequence = "Repite la secuencia",
                selectEnsemble = "¡Elige un ensemble!",
                ensembleNames = ensembleNamesEs,
                beatsPerMinute = "Pulsos por minuto",
                selectRhythm = "¡Elige un ritmo!",
                selectDoubling = "¡Elija intervalos para duplicar!",
                doublingNames = doublingEs,
                rhythm = "Ritmo",
                rhythmShuffle  = "Mezcla el ritmo",
                partsShuffle  = "Mezclar las partes",
                retrograde  = "Retrógrado",
                inverse  = "Inversión",
                invRetrograde  = "Retrógrado de la inversión",
                doubling  = "Duplicaciones",
                spreadWherePossible  = "Difundir donde sea posible",
                deepSearch  = "Búsqueda profunda en cánones de cuatro partes",
                exportMidi  = "Esporta el archivo MIDI",
                language  = "Lengua",
            )
        }
        fun german(): Lang {
            return Lang(
                noteNames = NoteNamesEn.values().map { it.toString() },
                enterSomeNotes = "Tippe ein paar Noten ein!",
                choose2ndSequence = "Wähle die zweite Sequenz!",
                repeatSequence = "Wiederhole die Sequenz",
                selectEnsemble = "Wähle ein Ensemble!",
                ensembleNames = ensembleNamesDe,
                beatsPerMinute = "Beats Per Minute",
                selectRhythm = "Wähle einen Rhythmus!",
                selectDoubling = "Wähle die Intervalle für die Verdoppelung!",
                doublingNames = doublingDe,
                rhythm = "Rhythmus",
                rhythmShuffle  = "Gemischter Rhythmus",
                partsShuffle  = "Gemischte Stimmen",
                retrograde  = "Krebs",
                inverse  = "Umkehrung",
                invRetrograde  = "Krebsumkehrung",
                doubling  = "Verdoppelungen",
                spreadWherePossible  = "Nach Möglichkeit verlängern",
                deepSearch  = "Tiefensuche in vierstimmigen Kanons",
                exportMidi  = "Exportieren Sie die MIDI-Datei",
                language  = "Sprache",
            )
        }
        fun russian(): Lang {
            return Lang(
                noteNames = NoteNamesRu.values().map { it.toString() },
                enterSomeNotes = "Введите ноты!",
                choose2ndSequence = "Выберите вторую последовательность!",
                repeatSequence = "Повторите последовательность",
                ensemble = "Ансамбль",
                bpm = "БПМ",
                selectEnsemble = "Выбери ансамбль!",
                ensembleNames = ensembleNamesRu,
                beatsPerMinute = "Удары в минуту",
                selectRhythm = "Выберите ритм!",
                selectDoubling = "Выбирайте интервалы для удвоения!",
                doublingNames = doublingRu,
                rhythm = "Ритм",
                rhythmShuffle  = "Смешанный ритм",
                partsShuffle  = "Смешанные голоса",
                retrograde  = "Ракоход",
                inverse  = "Инверсия",
                invRetrograde  = "Ракоход-инверсия",
                doubling  = "Двойной",
                spreadWherePossible  = "По возможности расширяйте",
                deepSearch  = "Глубокий поиск в четырехголосых канонах",
                exportMidi  = "Экспорт файла MIDI",
                language  = "Язык",
            )
        }
    }
}
