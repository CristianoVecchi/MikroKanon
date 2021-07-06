package com.cristianovecchi.mikrokanon.locale

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.composables.NoteNamesEn

enum class LANGUAGES(val language:String){
    ar("العربية"),
    de("Deutsch"), en("English"),es("Español"),
    fr("Français"), ko("한국어") ,jp("日本語"),
    it("Italiano"), ru("Русский"), zh("中文");
    companion object {
        fun languageNameFromDef(langDef: String): String {
            return when (langDef) {
                "ar" -> ar.language
                "de" -> de.language
                "en" -> en.language
                "es" -> es.language
                "fr" -> fr.language
                "ko" -> ko.language
                "jp" -> jp.language
                "it" -> it.language
                "ru" -> ru.language
                "zh" -> zh.language
                else -> en.language
            }
        }
    }
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
//enum class NoteNamesAr {
//    دو, ري, مي, فا, صول, لا,سي , EMPTY
//}
val ensembleNamesAr = listOf("آلة وترية ذات قوس", "آلة نفخ خشبية", "سلسلة الأوركسترا", "آلة نفخ نحاسية", "ساكسفون", "فلوت",
    "ضعف القصب", "كلارينيت", "مزمار", "تشيلو", "بيانو","بييرو")
val ensembleNamesDe = listOf("Streichinstrumente", "Holzblasinstrumente", "Streichorchester", "Blechblasinstrumente", "Saxophone", "Flauti",
    "Doppelblattinstrumente", "Klarinetten", "Fagotte", "Cellos", "Klavier","Pierrot")
val ensembleNamesEn = listOf("Strings", "Woodwinds", "String orchestra", "Brass", "Saxophones", "Flutes",
    "Double reeds", "Clarinets", "Bassoons", "Cellos", "Piano","Pierrot")
val ensembleNamesEs = listOf("Cuerdas", "Instrumentos de viento madera", "Orquesta de cuerdas", "Instrumentos de viento metal", "Saxofones", "Flautas",
    "Cañas dobles", "Clarinetes", "Fagotes", "Violonchelos", "Piano","Pierrot")
val ensembleNamesKo = listOf("찰현악기", "목관악기", "현악 합주단", "금관악기", "색소폰", "플루트",
    "더블 리드", "클라리넷", "바순", "첼로 스", "피아노","피에로")
val ensembleNamesJp = listOf("弦楽", "木管楽器", "弦楽オーケストラ", "金管楽器", "サックス", "フルート",
    "ダブルリード", "クラリネット", "ファゴット", "チェロ", "ピアノ","ピエロ")
val ensembleNamesIt = listOf("Archi", "Legni", "Orchestra d'archi", "Ottoni", "Saxofoni", "Flauti",
    "Ance doppie", "Clarinetti", "Fagotti", "Violoncelli", "Pianoforte","Pierrot")
val ensembleNamesFr = listOf("Cordes", "Bois", "Orchestre à cordes", "Cuivres", "Saxophones", "Flûtes",
    "Anches doubles", "Clarinettes", "Bassons", "Violoncelles", "Piano","Pierrot")
val ensembleNamesRu = listOf("Струнные", "Деревянные духовые инструменты", "Струнный оркестр", "Медные духовые инструменты", "Саксофоны", "Флейты",
    "Двойной тростью", "Кларнеты", "Фаготы", "Виолончели", "Фортепиано","Пьеро")
val ensembleNamesZh = listOf("弦乐", "木管乐器", "弦乐团", "銅管樂器", "薩氏管", "长笛",
    "双簧管", "单簧管", "巴松管", "大提琴", "钢琴","皮埃罗")


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
                "ar" -> arabian()
                "de" -> german()
                "en" -> english()
                "es" -> spanish()
                "fr" -> french()
                "ko" -> korean()
                "jp" -> japanese()
                "it" -> italian()
                "ru" -> russian()
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
                doubling  = "복식",
                spreadWherePossible  = "가능한 한 확장",
                deepSearch  = "네 가지 음성 음악 캐논에서 심층 검색",
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
                doubling  = "ダブルス",
                spreadWherePossible  = "可能な場合は延長する",
                deepSearch  = "4つの音声カノンでの詳細検索",
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
                doubling  = "双打",
                spreadWherePossible  = "尽可能延长",
                deepSearch  = "四个语音标准中的深度搜索",
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
                doubling  = "الزوجي",
                spreadWherePossible  = "قم بالتمديد حيثما أمكن ذلك",
                deepSearch  = "بحث عميق في أربعة شرائع صوتية",
                exportMidi  = "تصدير ملف MIDI",
                language  = "لغة",
            )
        }
    }
}
