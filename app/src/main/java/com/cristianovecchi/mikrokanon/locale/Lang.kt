package com.cristianovecchi.mikrokanon.locale

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.composables.NoteNamesEn

enum class LANGUAGES(val language:String){
    ar("العربية"),
    de("Deutsch"), el("Ελληνικά"),en("English"),es("Español"),
    fr("Français"), ko("한국어") ,jp("日本語"), id("Bahasa Indonesia"),
    it("Italiano"), pt("Português"), ru("Русский"),
    sw("Kiswahili"),zh("中文");
    companion object {
        fun languageNameFromDef(langDef: String): String {
            return when (langDef) {
                "ar" -> ar.language
                "de" -> de.language
                "el" -> el.language
                "en" -> en.language
                "es" -> es.language
                "fr" -> fr.language
                "ko" -> ko.language
                "jp" -> jp.language
                "id" -> id.language
                "it" -> it.language
                "pt" -> pt.language
                "ru" -> ru.language
                "sw" -> sw.language
                "zh" -> zh.language
                else -> en.language
            }
        }
    }
}


enum class NoteNamesPt {
    Dó,Ré,Mi,Fá,Sol,Lá,Si,EMPTY
}
enum class NoteNamesIt {
    Do,Re,Mi,Fa,Sol,La,Si,EMPTY
}
enum class NoteNamesFr {
    Ut,Ré,Mi,Fa,Sol,La,Si,EMPTY
}
enum class NoteNamesRu {
    До,Ре,Ми,Фа,Соль,Ля,Си,EMPTY
}
enum class NoteNamesEl {
    Ντο, Ρε, Μι, Φα, Σολ, Λα, Σι, EMPTY
}
//enum class NoteNamesAr {
//    دو, ري, مي, فا, صول, لا,سي , EMPTY
//}
val ensembleNamesAr = listOf("آلة وترية ذات قوس", "آلة نفخ خشبية", "سلسلة الأوركسترا", "آلة نفخ نحاسية", "ساكسفون", "فلوت",
    "ضعف القصب", "كلارينيت", "مزمار", "تشيلو", "بيانو","بييرو","الباروك","أوتار نتف")
val ensembleNamesDe = listOf("Streichinstrumente", "Holzblasinstrumente", "Streichorchester", "Blechblasinstrumente", "Saxophone", "Flauti",
    "Doppelblattinstrumente", "Klarinetten", "Fagotte", "Cellos", "Klavier","Pierrot","Barockensemble","Zupfinstrument")
val ensembleNamesEl = listOf("Έγχόρδα", "Ξύλινα πνευστά της συμφωνικής ορχήστρας", "Ορχήστρα εγχόρδων", "Χάλκινα πνευστά της συμφωνικής ορχήστρας", "Σαξόφωνα", "Φλάουτα",
    "Διπλά καλάμια", "Κλαρινέτ", "Φαγκότα", "Βιολοντσέλα", "Πιάνο", "Πιερότος","Μπαρόκ", "Ματαιωμένες χορδές")
val ensembleNamesEn = listOf("Strings", "Woodwinds", "String orchestra", "Brass", "Saxophones", "Flutes",
    "Double reeds", "Clarinets", "Bassoons", "Cellos", "Piano","Pierrot","Baroque","Plucked strings")
val ensembleNamesEs = listOf("Cuerdas", "Instrumentos de viento madera", "Orquesta de cuerdas", "Instrumentos de viento metal", "Saxofones", "Flautas",
    "Cañas dobles", "Clarinetes", "Fagotes", "Violonchelos", "Piano","Pierrot","Barroco","Instrumentos de cuerda pulsada")
val ensembleNamesKo = listOf("찰현악기", "목관악기", "현악 합주단", "금관악기", "색소폰", "플루트",
    "더블 리드", "클라리넷", "바순", "첼로 스", "피아노","피에로","바로크","발현악기")
val ensembleNamesJp = listOf("弦楽", "木管楽器", "弦楽オーケストラ", "金管楽器", "サックス", "フルート",
    "ダブルリード", "クラリネット", "ファゴット", "チェロ", "ピアノ","ピエロ", "バロック","撥弦楽器")
val ensembleNamesId = listOf("Alat musik dawai membungkuk", "Instrumen musik tiup kayu", "Orkestra dawai", "Instrumen musik tiup logam", "Saxophone", "Seruling",
    "Alang-alang ganda", "Klarinet", "Bassoon", "Cellos", "Piano", "Pierrot", "Baroque", "Dawai yang dipetik")
val ensembleNamesIt = listOf("Archi", "Legni", "Orchestra d'archi", "Ottoni", "Saxofoni", "Flauti",
    "Ance doppie", "Clarinetti", "Fagotti", "Violoncelli", "Pianoforte","Pierrot","Barocco", "Corde pizzicate")
val ensembleNamesPt = listOf("Cordas friccionadas", "Madeiras", "Orquestra de cordas", "Metais", "Saxofones", "Flautas",
    "Palhetas duplas", "Clarinetes", "Fagotes", "Violoncelos", "Piano", "Pierrot", "Barroco", "Cordas dedilhadas")
val ensembleNamesFr = listOf("Cordes", "Bois", "Orchestre à cordes", "Cuivres", "Saxophones", "Flûtes",
    "Anches doubles", "Clarinettes", "Bassons", "Violoncelles", "Piano","Pierrot","Baroque","Cordes pincées")
val ensembleNamesRu = listOf("Струнные", "Деревянные духовые инструменты", "Струнный оркестр", "Медные духовые инструменты", "Саксофоны", "Флейты",
    "Двойной тростью", "Кларнеты", "Фаготы", "Виолончели", "Фортепиано","Пьеро","Барокко","Струнные щипковые инструменты")
val ensembleNamesSw = listOf("Vyombo vilivyoinama", "Vyombo vya upepo vya mbao", "Orchestra ya ala za nyuzi", "Vyombo vya upepo vya chuma",
    "Saxophones", "Zilizimbi","Mwanzi mara mbili", "Clarinets", "Bassoons", "Cellos", "Piano",
    "Pierrot","Baroque", "Vyombo vya kamba vilivyokatwa")
val ensembleNamesZh = listOf("弦乐", "木管乐器", "弦乐团", "銅管樂器", "薩氏管", "长笛",
    "双簧管", "单簧管", "巴松管", "大提琴", "钢琴","皮埃罗","巴洛克","撥弦樂器")


val doublingDe = listOf("kleine Sekunde", "große Sekunde", "kleine Terz", "große Terz", "Quarte",
    "übermäßige Quarte", "Quinte", "kleine Sexte", "große Sexte", "kleine Septime", "große Septime",
    "Oktave", "kleine None", "große None", "kleine Dezime", "große Dezime", "Undezime",
    "übermäßige Undezime", "Duodezime", "kleine Tredezime ", "große Tredezime ", "kleine Quartdezime", "große Quartdezime", "Doppeloktave")
val doublingEl = listOf("Μικρή δευτέρα","Μεγάλη δευτέρα", "Μικρή τρίτη", "Μεγάλη τρίτη", "Καθαρή τετάρτη",
    "Αυξημένη τετάρτη", "Καθαρή πέμπτη", "Μικρή έκτη", "Μεγάλη έκτη", "Μικρή εβδόμη", "Μεγάλη εβδόμη",
    "Οκτάβα", "Μικρή ένατη", "Μεγάλη ένατη", "Μικρή δέκατη", "Μεγάλη δέκατη", "Καθαρή ενδέκατη",
    "Αυξημένη ενδέκατη", "Καθαρή δωδέκατη", "Μικρή δέκατη τρίτη", "Μεγάλη δέκατη τρίτη", "Μικρή δέκατη τέταρτη", "Μεγάλη δέκατη τέταρτη", "Καθαρή δέκατη πέμπτη")
val doublingEn = listOf("minor 2nd","Major 2nd", "minor 3rd", "Major 3rd", "4th",
    "Augm. 4th", "5th", "minor 6th", "Major 6th", "minor 7th", "Major 7th",
    "Octave", "minor 9th", "Major 9th", "minor 10th", "Major 10th", "11th",
    "Augm. 11th", "12th", "minor 13th", "Major 13th", "minor 14th", "Major 14th", "Double Octave")
val doublingId = listOf("Minor kedua","Mayor kedua", "Minor ketiga", "Mayor ketiga", "Sempurna keempat",
    "Tambah keempat", "Sempurna kelima", "Minor keenam", "Mayor keenam", "Minor ketujuh", "Mayor ketujuh",
    "Oktaf","Minor kedua + oktaf","Mayor kedua + oktaf", "Minor ketiga + oktaf", "Mayor ketiga + oktaf", "Sempurna keempat + oktaf",
    "Tambah keempat + oktaf", "Sempurna kelima + oktaf", "Minor keenam + oktaf", "Mayor keenam + oktaf",
    "Minor ketujuh + oktaf", "Mayor ketujuh + oktaf",
    "Oktaf ganda" )
val doublingIt = listOf("2ª minore","2ª Maggiore", "3ª minore", "3ª Maggiore", "4ª",
    "4ª Aumentata", "5ª", "6ª minore", "6ª Maggiore", "7ª minore", "7ª Maggiore",
    "Ottava", "9ª minore", "9ª Maggiore", "10ª minore", "10ª Maggiore", "11ª",
    "11ª Aumentata", "12ª", "13ª minore", "13ª Maggiore", "14ª minore", "14ª Maggiore", "Ottava doppia")
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
val doublingKo = listOf("단2도","장2도","단3도","장3도","완전4도",
    "트라이톤","완전5도","단6도","장6도","단7도","장7도",
    "완전8도","단9도","장9도","단10도","장10도","완전11도",
    "완전8도+트라이톤","완전12도","단13도","장13도","단14도","장14도",
    "완전15도")
val doublingJp = listOf("短2度","長2度","短3度","長3度","4度",
"増4度","5度","短6度","長6度","短7度","長7度",
"8度","短9度","長9度","短10度","長10度","11度",
    "増11度","12度","短13度","長13度","短14度","長14度","15度")
val doublingZh = listOf("小二度","大二度","小三度","大三度","纯四度",
    "增四度","纯五度","小六度","大六度","小七度","大七度",
    "纯八度","小九度","大九度","小十度","大十度","纯十一度",
    "增十一度","纯十二度","小十三度","大十三度","小十四度","大十四度",
    "纯十五度")
val doublingSw = listOf("Ndogo ya pili", "Kubwa ya pili", "Ndogo ya tatu", "Kubwa ya tatu", "Ya nne",
    "Ya nne kupindukia", "Ya tano", "Ndogo ya sita", "Kubwa ya sita", "Ndogo ya saba", "Kubwa ya saba",
    "Octave", "Ndogo Hakuna", "Kubwa Hakuna", "Ndogo ya zaka", "Kubwa ya zaka", "Kumi na moja",
    "Kumi na moja kupindukia", "Kumi na mbili", "Ndogo kumi na tatu", "Kubwa kumi na tatu",
    "Ndogo kumi na nne", "Kubwa kumi na nne", "Octave mara mbili")
val doublingPt = listOf("2ª menor","2ª Maior", "3ª menor", "3ª Maior", "4ª",
    "4ª Aumentada", "5ª", "6ª menor", "6ª Maior", "7ª menor", "7ª Maior",
    "Oitava", "9ª menor", "9ª Maior", "10ª menor", "10ª Maior", "11ª",
    "11ª Aumentada", "12ª", "13ª menor", "13ª Maior", "14ª menor", "14ª Maior", "Oitava dupla")
val functionNamesEn = listOf("Wave 3", "Wave 4", "Wave 6")
val intervalSetIt = listOf("2m\n7M","2M\n7m","3m\n6M","3M\n6m","4\n5","4A\n5d","U\n8")
val intervalSetEn = listOf("m2\nM7","M2\nm7","m3\nM7","M3\nm6","4\n5","A4\nd5","U\n8")
val intervalSetDe = listOf("k2\nG7","G2\nk7","k3\nG7","G3\nk6","4\n5","Ü4\nv5","1\n8")
val intervalSetRu = listOf("2м\n7В","2В\n7м","3м\n6В","3В\n6м","4\n5","4У\n5у","1\n8")
data class Lang( // English by default
    val noteNames: List<String> = NoteNamesEn.values().map { it.toString() },
    val intervalSet: List<String> = intervalSetEn,
    val enterSomeNotes: String = "Enter some notes!",
    val choose2ndSequence: String = "Choose the second sequence!",
    val repeatSequence: String = "Repeat the sequence",
    val selectSpecialFunction: String = "Select a Special Function!",
    val specialFunctionNames: List<String> = functionNamesEn,
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
    val rowFormSeparator: String = "Row form separator",
    val ritornello: String = if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O) String(Character.toChars(0x1D106)) + "  " + String(Character.toChars(0x1D107))
                            else "Ritornello",
    val selectRitornello: String = "Select how many repetitions!",
    val doubling: String = "Doubling",
    val spreadWherePossible: String = "Spread where possible",
    val deepSearch: String = "Deep search in four-part canons",
    val horIntervalSet: String = "Free part intervals",
    val selectIntervalsForFP: String = "Select the melodic intervals for the Fioritura and the free parts!",
    val FPremember: String = "(  ☀  ∼➚  ∼➘  -➚  -➘  )   ",
    val detector: String = "Detector",
    val selectIntervalsToDetect: String = "Select intervals to detect!",
    val detectorExtension: String = "Detector extension",
    val selectDetectorExtension: String = "Select the extension for the detector!",
    val exportMidi: String = "Export MIDI",
    val playToCreate: String = "Play a counterpoint to create a MIDI file!",
    val language: String = "Language",
    val credits: String = "Credits"
    ){


    companion object {
        fun provideLanguage(lang: String): Lang {
            return when (lang){
                "ar" -> arabian()
                "de" -> german()
                "el" -> greek()
                "en" -> english()
                "es" -> spanish()
                "fr" -> french()
                "ko" -> korean()
                "jp" -> japanese()
                "id" -> bahasa()
                "it" -> italian()
                "pt" -> portugues()
                "ru" -> russian()
                "sw" -> kiswahili()
                "zh" -> chinese()
                else -> Lang()
            }
        }
        fun english(): Lang {
            return Lang()
        }
        fun french(): Lang {
            return Lang(
                noteNames = NoteNamesFr.values().map { it.toString() },
                intervalSet = intervalSetIt,
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
                partsShuffle  = "Mélanger les voix",
                retrograde  = "Rétrograde",
                inverse  = "Miroir",
                invRetrograde  = "Miroir du rétrograde",
                rowFormSeparator = "Séparateur de forme série",
                doubling  = "Redoublements ", // let a space because in french [word] [_] [:|!|?] [_]
                spreadWherePossible  = "Répandre là où c'est possible",
                deepSearch  = "Recherche poussée dans les canons à quatre voix",
                horIntervalSet = "Intervalles de voix libres",
                selectIntervalsForFP = "Choisissez les intervalles mélodiques pour les voix libres!",
                exportMidi  = "Exporter le fichier MIDI",
                language  = "Langue ",
            )
        }
        fun italian(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetIt,
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
             rowFormSeparator = "Separatore delle forme seriali",
             doubling  = "Raddoppi",
             spreadWherePossible  = "Estendi dove è possibile",
             deepSearch  = "Ricerca approfondita nei canoni a quattro parti",
                horIntervalSet = "Intervalli delle parti libere",
                selectIntervalsForFP = "Scegli gli intervalli melodici per le fioriture e le parti libere!",
             exportMidi  = "Esporta il file MIDI",
             language  = "Lingua",
            )
        }
        fun spanish(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetIt,
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
                rhythmShuffle  = "Mezclar el ritmo",
                partsShuffle  = "Mezclar las voces ",
                retrograde  = "Retrógrado",
                inverse  = "Inversión",
                invRetrograde  = "Retrógrado de la inversión",
                rowFormSeparator = "Separador de forma de serie",
                doubling  = "Duplicaciones",
                spreadWherePossible  = "Difundir donde sea posible",
                deepSearch  = "Búsqueda profunda en cánones de cuatro partes",
                horIntervalSet = "Intervalos de voces libres",
                selectIntervalsForFP = "Elige los intervalos melódicos de las voces libres!",
                exportMidi  = "Esporta el archivo MIDI",
                language  = "Lengua",
            )
        }
        fun german(): Lang {
            return Lang(
                noteNames = NoteNamesEn.values().map { it.toString() },
                intervalSet = intervalSetDe,
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
                rowFormSeparator = "Trennzeichen für serielle Formulare",
                doubling  = "Verdoppelungen",
                spreadWherePossible  = "Nach Möglichkeit verlängern",
                deepSearch  = "Tiefensuche in vierstimmigen Kanons",
                horIntervalSet = "Intervalle freier Stimmen",
                selectIntervalsForFP = "Wählen Sie die melodischen Intervalle der freien Stimmen!",
                exportMidi  = "Exportieren Sie die MIDI-Datei",
                language  = "Sprache",
            )
        }
        fun russian(): Lang {
            return Lang(
                noteNames = NoteNamesRu.values().map { it.toString() },
                intervalSet = intervalSetRu,
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
                rowFormSeparator = "Разделитель серийных форм",
                doubling  = "Двойной",
                spreadWherePossible  = "По возможности расширяйте",
                deepSearch  = "Глубокий поиск в четырехголосых канонах",
                horIntervalSet = "Интервалы свободных голосов",
                selectIntervalsForFP = "Выберите мелодические интервалы свободных голосов!",
                exportMidi  = "Экспорт файла МИДИ",
                language  = "Язык",
            )
        }
        fun korean(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                enterSomeNotes = "음표를 입력하세요!",
                choose2ndSequence = "두 번째 시퀀스를 선택하십시오!",
                repeatSequence = "순서를 반복하십시오",
                selectEnsemble = "앙상블을 선택하십시오!",
                ensembleNames = ensembleNamesKo,
                beatsPerMinute = "분당 박동",
                ensemble = "앙상블",
                selectRhythm = "리듬을 선택하세요!",
                selectDoubling = "배가 간격을 선택하십시오!",
                doublingNames = doublingKo,
                rhythm = "율",
                rhythmShuffle  = "혼합 된 리듬",
                partsShuffle  = "혼성",
                retrograde  = "뒤로 걷다",
                inverse  = "역 동작",
                invRetrograde  = "움직임을 뒤집고 뒤로 걸어",
                rowFormSeparator = "직렬 형식 구분 기호",
                doubling  = "복식",
                spreadWherePossible  = "가능한 한 확장",
                deepSearch  = "네 가지 음성 음악 캐논에서 심층 검색",
                horIntervalSet = "자유로운 목소리의 간격",
                selectIntervalsForFP = "자유로운 목소리의 멜로디 간격을 선택하십시오!",
                exportMidi  = "MIDI 파일 내보내기",
                language  = "언어",
            )
        }
        fun japanese(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                enterSomeNotes = "音符を入力してください！",
                choose2ndSequence = "2番目のシーケンスを選択してください！",
                repeatSequence = "シーケンスを繰り返します",
                selectEnsemble = "アンサんブルを選いしてください！",
                ensembleNames = ensembleNamesJp,
                beatsPerMinute = "分あたりの拍数",
                ensemble = "アンサンブル",
                selectRhythm = "リズムを選んでください！",
                selectDoubling = "倍増の間隔を選択してください！",
                doublingNames = doublingJp,
                rhythm = "リズム",
                rhythmShuffle  = "混合リズム",
                partsShuffle  = "混合声",
                retrograde  = "後ろ向きに歩く",
                inverse  = "間隔の反転",
                invRetrograde  = "後ろ向きに歩くことで間隔を逆にする",
                rowFormSeparator = "シリアルフォームセパレータ",
                doubling  = "ダブルス",
                spreadWherePossible  = "可能な場合は延長する",
                deepSearch  = "4つの音声カノンでの詳細検索",
                horIntervalSet = "自由な声の間隔",
                selectIntervalsForFP = "フリーボイスのメロディー間隔を選択してください！",
                exportMidi  = "MIDIファイルをエクスポートする",
                language  = "言語",
            )
        }
        fun chinese(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                enterSomeNotes = "键入一些音符时间",
                choose2ndSequence = "选择第二个序列！",
                repeatSequence = "重复序列",
                selectEnsemble = "选择合奏！",
                ensembleNames = ensembleNamesZh,
                beatsPerMinute = "每分钟节拍",
                ensemble = "合奏",
                selectRhythm = "选择节奏！",
                selectDoubling = "选择度数加倍！",
                doublingNames = doublingZh,
                rhythm = "韵律",
                rhythmShuffle  = "混合节奏",
                partsShuffle  = "混合的声音",
                retrograde  = "向后走",
                inverse  = "反转间隔",
                invRetrograde  = "向后走并反转间隔",
                rowFormSeparator = "序列表分隔符",
                doubling  = "双打",
                spreadWherePossible  = "尽可能延长",
                deepSearch  = "四个语音标准中的深度搜索",
                horIntervalSet = "自由声音的间隔",
                selectIntervalsForFP = "选择自由声音的旋律音程！",
                exportMidi  = "导出 MIDI 文件",
                language  = "语",
            )
        }

        fun arabian(): Lang {
            return Lang(
                noteNames = listOf("دو","ري","مي","فا","صول","لا","سي"),
                enterSomeNotes = "!اكتب بعض النوتات الموسيقية",
                choose2ndSequence = "!اختر التسلسل الثاني",
                repeatSequence = "كرر التسلسل",
                ensemble ="الفرقة",
                selectEnsemble = "!اختر مجموعة",
                ensembleNames = ensembleNamesAr,
                beatsPerMinute = "نبضة في الدقيقة",
                selectRhythm = "!اختر إيقاعًا",
                selectDoubling = "!اختر فترات لمضاعفة",
                doublingNames = doublingIt,
                rhythm = "إيقاع",
                rhythmShuffle  = "إيقاع مختلط",
                partsShuffle  = "اصوات مختلطة",
                retrograde  = "المشي إلى الخلف",
                inverse  = "اعكس الحركات",
                invRetrograde  = "اعكس الحركات وامش للخلف",
                rowFormSeparator = "فاصل الشكل التسلسلي",
                doubling  = "الزوجي",
                spreadWherePossible  = "قم بالتمديد حيثما أمكن ذلك",
                deepSearch  = "بحث عميق في أربعة شرائع صوتية",
                horIntervalSet = "فترات من الأصوات الحرة",
                selectIntervalsForFP = "!اختر الفترات اللحنية للأصوات المجانية",
                exportMidi  = "تصدير ملف MIDI",
                language  = "لغة",
            )
        }
        fun greek(): Lang {
            return Lang(
                noteNames = NoteNamesEl.values().map { it.toString() },
                intervalSet = intervalSetEn,
                enterSomeNotes = "Πληκτρολογήστε μερικές νότες!",
                choose2ndSequence = "Επιλέξτε τη δεύτερη ακολουθία!",
                repeatSequence = "Επαναλάβετε την ακολουθία",
                ensemble ="Σύνολο",
                bpm = "ΒΠΜ",
                selectEnsemble = "Επιλέξτε ένα σύνολο!",
                ensembleNames = ensembleNamesEl,
                beatsPerMinute = "Χτυπάει ανά λεπτό",
                selectRhythm = "Επιλέξτε ρυθμό!",
                selectDoubling = "Επιλέξτε διαστήματα για διπλασιασμό!",
                doublingNames = doublingEl,
                rhythm = "Ρυθμός",
                rhythmShuffle  = "Μικτός ρυθμός",
                partsShuffle  = "Φωνές μικτές",
                retrograde  = "Τον καρκίνο",
                inverse  = "Την αναστροφή ",
                invRetrograde  = "Την καρκινική αναστροφή",
                rowFormSeparator = "Διαχωριστικό σειριακής φόρμας",
                doubling  = "Διπλασιάζω",
                spreadWherePossible  = "Επεκτείνετε όπου είναι δυνατόν",
                deepSearch  = "Βαθιά αναζήτηση σε τέσσερις φωνητικούς κανόνες",
                horIntervalSet = "Διαστήματα ελεύθερων φωνών",
                selectIntervalsForFP = "Επιλέξτε τα μελωδικά διαστήματα των ελεύθερων φωνών!",
                exportMidi  = "Εξαγωγή του αρχείου MIDI",
                language  = "Γλώσσα",
            )
        }
        fun kiswahili(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetIt,
                enterSomeNotes = "Chapa maelezo kadhaa!",
                choose2ndSequence = "Chagua mlolongo wa pili!",
                repeatSequence = "Kurudia mlolongo",
                selectEnsemble = "Chagua mkusanyiko!",
                ensembleNames = ensembleNamesSw,
                beatsPerMinute = "Beats kwa dakika",
                selectRhythm = "Chagua dansi!",
                selectDoubling = "Chagua vipindi vya kuongezeka mara mbili!",
                doublingNames = doublingSw,
                rhythm = "Mdundo",
                rhythmShuffle  = "Mdundo mchanganyiko",
                partsShuffle  = "Sauti zilizochanganywa",
                retrograde  = "Tembea nyuma",
                inverse  = "Kubadili harakati",
                invRetrograde  = "Tembea nyuma na kubadili harakati",
                rowFormSeparator = "Separator ya fomu ya serial",
                doubling  = "Maradufu",
                spreadWherePossible  = "Panua panapowezekana",
                deepSearch  = "Utafutaji wa kina katika kanuni nne za sauti",
                horIntervalSet = "Vipindi vya sauti za bure",
                selectIntervalsForFP = "Chagua vipindi vya sauti ya sauti za bure!",
                exportMidi  = "Hamisha faili ya MIDI",
                language  = "Lugha",
            )
        }
        fun portugues(): Lang {
            return Lang(
                noteNames = NoteNamesPt.values().map { it.toString() },
                intervalSet = intervalSetIt,
                enterSomeNotes = "Digite algumas notas!",
                choose2ndSequence = "Escolha a segunda sequência!",
                repeatSequence = "Repita a sequência",
                ensemble ="Conjunto",
                selectEnsemble = "Escolha um conjunto!",
                ensembleNames = ensembleNamesPt,
                beatsPerMinute = "Batimentos por minuto",
                selectRhythm = "Escolha um ritmo!",
                selectDoubling = "Escolha intervalos para dobrar!",
                doublingNames = doublingPt,
                rhythm = "Ritmo",
                rhythmShuffle  = "Misture o ritmo",
                partsShuffle  = "Misture as vozes ",
                retrograde  = "Retrogradação",
                inverse  = "Inversão",
                invRetrograde  = "Inverso de retrogradação",
                rowFormSeparator = "Separador de forma serial",
                doubling  = "Duplas",
                spreadWherePossible  = "Estenda onde for possível",
                deepSearch  = "Pesquisa profunda em cânones de quatro vozes",
                horIntervalSet = "Intervalos de vozes livres",
                selectIntervalsForFP = "Escolha os intervalos melódicos de vozes livres",
                exportMidi  = "Exporte o arquivo MIDI",
                language  = "Língua",
            )
        }
        fun bahasa(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetEn,
                enterSomeNotes = "Ketik beberapa not!",
                choose2ndSequence = "Scegli la seconda sequenza!",
                repeatSequence = "Pilih urutan kedua!",
                ensemble = "Ansambel",
                selectEnsemble = "Pilih ansambel!",
                ensembleNames = ensembleNamesId,
                beatsPerMinute = "Detak per menit",
                selectRhythm = "Pilih irama!",
                selectDoubling = "Pilih interval untuk menggandakan!",
                doublingNames = doublingId,
                rhythm = "Irama",
                rhythmShuffle  = "Campurkan irama",
                partsShuffle  = "Campur suara",
                retrograde  = "Berjalan mundur",
                inverse  = "Membalikkan gerakan",
                invRetrograde  = "Membalikkan gerakan dan berjalan mundur",
                rowFormSeparator = "Pemisah bentuk serial",
                doubling  = "Ganda",
                spreadWherePossible  = "Perluas jika memungkinkan",
                deepSearch  = "Pencarian mendalam dalam empat kanon suara",
                horIntervalSet = "Interval suara bebas",
                selectIntervalsForFP = "Pilih interval melodi dari suara bebas!",
                exportMidi  = "Ekspor file MIDI",
                language  = "Bahasa",
            )
        }
    }
}
