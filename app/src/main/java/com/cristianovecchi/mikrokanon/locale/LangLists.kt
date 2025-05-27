package com.cristianovecchi.mikrokanon.locale

enum class NoteNamesPt {
    Dó,Ré,Mi,Fá,Sol,Lá,Si,EMPTY
}
// Icelandic and German use C D E F G A H/B
//enum class NoteNames?? {
//    Do,Re,Mí,Fa,Sol,La,Tí,EMPTY
//}
enum class NoteNamesIt {
    Do,Re,Mi,Fa,Sol,La,Si,EMPTY
}
enum class NoteNamesFr {
    Ut,Ré,Mi,Fa,Sol,La,Si,EMPTY
}
enum class NoteNamesRu {
    До,Ре,Ми,Фа,Соль,Ля,Си,EMPTY
}
enum class NoteNamesUk {
    До,Ре,Мі,Фа,Соль,Ля,Сі,EMPTY
}
enum class NoteNamesEl {
    Ντο, Ρε, Μι, Φα, Σολ, Λα, Σι, EMPTY
}
//enum class NoteNamesAr {
//    دو, ري, مي, فا, صول, لا,سي , EMPTY
//}

val synthsNames = listOf(
    "Nylon Guitar", "Steel Guitar", "Jazz Guitar", "Clean Guitar", "Muted Guitar", "Overdrive Guitar", "Distortion Guitar", "Banjo",
    "Acoustic Bass", "Fretless Bass", "Slap Bass", "Syn Bass",
    "Tremolo strings", "Pizzicato", "Fiddle", "Muted brass",
    "Bag pipes", "Recorders", "Shanai", "Sitar", "Shamisen", "Koto",
    "Harpsichord", "Xylophone", "Marimba", "Kalimba", "Vibraphone", "Glockenspiel", "Celesta", "Bells", "Tinkle Bells", "Agogo", "Steel Drums", "Reverse Cymbals",
    "Timpani", "Woodblocks", "Taiko Drums", "Melodic Toms", "Syn Drums",
    "Electric Piano 1", "Electric Piano 2",
    "Hammond Organ","Perc. Organ","Blues Organ","Church Organ","Reed Organ","Accordion","Tango Accordion",
    "Square Wave","Saw Wave", "Calliope", "Chiff",
    "Charang", "Synth Voice", "Fifths Saw", "Brass and Lead",
    "Fantasia", "Warm Pad", "Polysynth", "Space Vox",
    "Bowed Glass", "Metal Pad", "Halo Pad", "Sweep Pad",
    "Ice Rain", "Soundtrack", "Crystal", "Atmosphere",
    "Brightness", "Goblins", "Echo Drops", "Sci Fi")

val ensembleNamesAr = listOf("آلة وترية ذات قوس", "آلة نفخ خشبية", "سلسلة الأوركسترا", "آلة نفخ نحاسية","غاميلان", "ساكسفون", "فلوت",
    "ضعف القصب", "كلارينيت","بوق فرنسي",  "مزمار", "تشيلو", "بيانو","قيثار","بييرو","الباروك","أوتار نتف","جوقة","مرعب")
val ensembleNamesDe = listOf("Streichinstrumente", "Holzblasinstrumente", "Streichorchester", "Blechblasinstrumente","Gamelan", "Saxophone", "Flöten",
    "Doppelblattinstrumente", "Klarinetten", "Hörner", "Fagotte", "Cellos", "Klavier","Harfe","Pierrot","Barockensemble","Zupfinstrument","Chor","Gespenstisch")
val ensembleNamesEl = listOf("Έγχόρδα", "Ξύλινα πνευστά της συμφωνικής ορχήστρας", "Ορχήστρα εγχόρδων", "Χάλκινα πνευστά της συμφωνικής ορχήστρας","Gamelan", "Σαξόφωνα", "Φλάουτα",
    "Διπλά καλάμια", "Κλαρινέτ", "Κέρατα", "Φαγκότα", "Βιολοντσέλα", "Πιάνο","Άρπα", "Πιερότος","Μπαρόκ", "Ματαιωμένες χορδές","Χορωδία","Στοιχειωμένος")
val ensembleNamesEn = listOf("Strings", "Woodwinds", "String orchestra", "Brass", "Gamelan", "Saxophones", "Flutes",
    "Double reeds", "Clarinets", "French horns", "Bassoons", "Cellos", "Piano","Harp","Pierrot","Baroque","Plucked strings","Choir","Spooky")
val ensembleNamesEs = listOf("Cuerdas", "Instrumentos de viento madera", "Orquesta de cuerdas", "Instrumentos de viento metal","Gamelán", "Saxofones", "Flautas",
    "Cañas dobles", "Clarinetes", "Trompas", "Fagotes", "Violonchelos", "Piano","Arpa", "Pierrot","Barroco","Instrumentos de cuerda pulsada","Coro","Escalofriante")
val ensembleNamesFi = listOf("Jousisoittimet", "Puupuhaltimet", "Jousiorkesteri", "Vaskipuhaltimet", "Gamelan", "Saksofonit", "Huilut",
    "Kaksoisruoko", "Klarinetit", "Käyrätorvit", "Fagottit", "Sellot", "Piano", "Harppu", "Pierrot", "Barokki", "Kynnitty kielet", "Kuoro", "Aavemainen")
val ensembleNamesFr = listOf("Cordes", "Bois", "Orchestre à cordes", "Cuivres", "Gamelan","Saxophones", "Flûtes",
    "Anches doubles", "Clarinettes", "Cors", "Bassons", "Violoncelles", "Piano","Harpe", "Pierrot","Baroque","Cordes pincées","Chorale","Sinistre")
val ensembleNamesKo = listOf("찰현악기", "목관악기", "현악 합주단", "금관악기","가믈란", "색소폰", "플루트",
    "더블 리드", "클라리넷", "프렌치 호른", "바순", "첼로 스", "피아노","하프","피에로","바로크","발현악기","합창","유령 같은")
val ensembleNamesJa = listOf("弦楽", "木管楽器", "弦楽オーケストラ", "金管楽器","ガムラン","サックス", "フルート",
    "ダブルリード", "クラリネット", "ホルン", "ファゴット", "チェロ", "ピアノ","ハープ", "ピエロ", "バロック","撥弦楽器","合唱","不気味な")
val ensembleNamesHi = listOf("झुका हुआ यंत्र", "वुडविंड उपकरण", "स्ट्रिंग ऑर्केस्ट्रा", "धातु पवन यंत्र","गमेलन", "सैक्सोफोन", "बांसुरी",
    "डबल रीड", "क्लैरिनेट", "फ्रेंच हॉर्न", "बासून", "सेलोस", "पियानो","वीणा","पिय्रोट","बरोक","प्लक्ड स्ट्रिंग इंस्ट्रूमेंट्स","वृन्दगान","डरावना")
val ensembleNamesId = listOf("Alat musik dawai membungkuk", "Instrumen musik tiup kayu", "Orkestra dawai", "Instrumen musik tiup logam", "Gamelan","Saxophone", "Seruling",
    "Alang-alang ganda", "Klarinet", "Tanduk", "Bassoon", "Cellos", "Piano", "Harpa", "Pierrot", "Baroque", "Dawai yang dipetik","Paduan suara","Menyeramkan")
val ensembleNamesIs = listOf("Strengir", "Blásarhljóðfæri", "Strengjasveit", "Málmblásturshljóðfæri", "Gamelan", "Saxófónar", "Flautur",
    "Tvöfaldur reyr", "Klarínettur", "Frönsk horn", "Fagottar", "Selló", "Píanó", "Harpa", "Pierrot", "Barokk", "Plokkaðir strengir", "Kór", "Skuggalegt")
val ensembleNamesIt = listOf("Archi", "Legni", "Orchestra d'archi", "Ottoni","Gamelan", "Saxofoni", "Flauti",
    "Ance doppie", "Clarinetti", "Corni", "Fagotti", "Violoncelli", "Pianoforte","Arpa","Pierrot","Barocco", "Corde pizzicate","Coro","Spooky")
val ensembleNamesPl = listOf("Smyczki", "Dęte drewniane", "Orkiestra smyczkowa", "Instrumenty dęte blaszane", "Gamelan", "Saksofony", "Flety",
    "Podwójne stroiki", "Klarnety", "Waltornie", "Fagoty", "Wiolonczele", "Fortepian", "Harfa", "Pierrot", "Barokowy", "Sznurki szarpane", "Chór", "Straszny")
val ensembleNamesPt = listOf("Cordas friccionadas", "Madeiras", "Orquestra de cordas", "Metais","Gamelão","Saxofones", "Flautas",
    "Palhetas duplas", "Clarinetes", "Trompas", "Fagotes", "Violoncelos", "Piano","Harpa", "Pierrot", "Barroco", "Cordas dedilhadas","Coro","Assustador")
val ensembleNamesRu = listOf("Струнные", "Деревянные духовые инструменты", "Струнный оркестр", "Медные духовые инструменты","Гамелан", "Саксофоны", "Флейты",
    "Двойной тростью", "Кларнеты", "Валторны", "Фаготы", "Виолончели", "Фортепиано","Арфа", "Пьеро","Барокко","Струнные щипковые инструменты","Хор","Пугающий")
val ensembleNamesSw = listOf("Vyombo vilivyoinama", "Vyombo vya upepo vya mbao", "Orchestra ya ala za nyuzi", "Vyombo vya upepo vya chuma",
    "Gamelan", "Saxophones", "Zilizimbi","Mwanzi mara mbili", "Clarinets", "Pembe za Kifaransa", "Bassoons", "Cellos", "Piano", "Kinubi",
    "Pierrot","Baroque", "Vyombo vya kamba vilivyokatwa","Kwaya","Ya kutisha")
val ensembleNamesTh = listOf("เครื่องสาย", "เครื่องลมไม้", "เครื่องสายออร์เคสตรา", "ทองเหลือง", "กาเมลัน", "แซกโซโฟน", "ฟลุต",
    "ไม้อ้อคู่", "คลาริเน็ต", "เฟรนช์ฮอร์น", "บาสซูน", "เชลโล", "เปียโน", "ฮาร์ป", "เปียโรต์", "บาโรก", "ตามแบบบะโรค", "นักร้องประสานเสียง", "น่ากลัว")
val ensembleNamesUk = listOf("Смичкові", "Дерев'яні духові", "Струнний оркестр", "Мідні духові", "Гамелан", "Саксофони", "Флейти",
    "Подвійний очерет", "Кларнети", "Валторни", "Фаготи", "Віолончелі", "фортепіано","арфа", "П'єро","бароко","Пощипані струни","Хор","моторошний")
val ensembleNamesZh = listOf("弦乐", "木管乐器", "弦乐团", "銅管樂器","甘美蘭", "薩氏管", "长笛",
    "双簧管", "单簧管", "圆号", "巴松管", "大提琴", "钢琴","豎琴","皮埃罗","巴洛克","撥弦樂器","合唱","幽灵般的")

var zodiacPlanets = listOf("\u2644", "\u2643", "\u2642","\u2640","\u263F","\u263D","\u2609")
private var zodiacPlanetsEmojis = listOf("\u1fA90","\u9795","\u9794","\u9792","\u9791","\u1F31C","\u1F31E") // are different on xiaomi
val zodiacSignsEmojis = listOf("\u2648","\u2649","\u264A","\u264B","\u264C","\u264D","\u264E","\u264F","\u2650","\u2651","\u2652","\u2653",)
val zodiacSigns = listOf("♈︎","♉︎","♊︎","♋︎","♌︎","♍︎","♎︎","♏︎","♐︎","♑︎","♒︎","♓︎",)

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
val doublingFi = listOf("Pieni sekunti","Suuri sekunti", "Pieni terssi", "Suuri terssi", "Puhdas kvartti",
    "Ylinouseva kvartti", "Puhdas kvintti", "Pieni seksti", "Suuri seksti", "Pieni septimi", "Suuri septimi",
    "Puhdas oktaavi", "Pieni sekunti + oktaavi", "Suuri sekunti + oktaavi", "Pieni terssi + oktaavi", "Suuri terssi + oktaavi", "Puhdas kvartti + oktaavi",
    "Ylinouseva kvartti + oktaavi", "Puhdas kvintti + oktaavi", "Pieni seksti + oktaavi", "Suuri seksti + oktaavi", "Pieni septimi + oktaavi", "Suuri septimi + oktaavi", "Oktaavi + oktaavi")
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
    "Квартдецима малая", "Квартдецима большая", "Квинтдецима")
val doublingKo = listOf("단2도","장2도","단3도","장3도","완전4도",
    "트라이톤","완전5도","단6도","장6도","단7도","장7도",
    "완전8도","단9도","장9도","단10도","장10도","완전11도",
    "완전8도+트라이톤","완전12도","단13도","장13도","단14도","장14도",
    "완전15도")
val doublingJa = listOf("短2度","長2度","短3度","長3度","4度",
    "増4度","5度","短6度","長6度","短7度","長7度",
    "8度","短9度","長9度","短10度","長10度","11度",
    "増11度","12度","短13度","長13度","短14度","長14度","15度")
val doublingUk = listOf("мала Секунда", "велика Секунда", "мала Терція", "велика Терція", "Кварта",
    "збільшена Кварта", "Квінта", "мала Секста", "велика Секста", "мала Септима", "велика Септима",
    "Октава", "мала Нона", "велика Нона", "мала Децима", "велика Децима", "Ундецима",
    "збільшена Ундецима", "Дуодецима", "мала Терцдецима", "велика Терцдецима",
    "мала Квартдецима", "велика Квартдецима", "Квинтдецима")
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
val intervalSetEn = listOf("m2\nM7","M2\nm7","m3\nM6","M3\nm6","4\n5","A4\nd5","U\n8")
val intervalSetFi = listOf("p2\nS7","S2\np7","p3\nS6","S3\np6","4\n5","y4\nV5","U\n8")
val intervalSetDe = listOf("k2\nG7","G2\nk7","k3\nG6","G3\nk6","4\n5","Ü4\nv5","1\n8")
val intervalSetRu = listOf("2м\n7В","2В\n7м","3м\n6В","3В\n6м","4\n5","4У\n5у","1\n8")
val intervalSetUk = listOf("м2\nВ7","В2\nм7","м3\nВ6","В3\nм6","4\n5","З4\nч5","1\n8")

val rowFormsMap = mapOf(
    1 to "O", 2 to "I" , 3 to "R", 4 to "IR", -1 to "O |", -2 to "I |" , -3 to "R |", -4 to "IR |"
)
//"≈≈≈≈≈", "√√√√√", "➚➚➚➚➚", "➘➘➘➘➘")
val melodyTypeMap = mapOf(
    0 to "≈", 1 to "√", 2 to "➚", 3 to "➘", 4 to "⇅", 5 to "R"
)
val rangeTypeMap = mapOf(
    0 to "∞", 1 to "I", 2 to "[--]", 3 to "[-]", 4 to "[ ]"
)
val legatoTypeMap = mapOf(
    0 to "S+", 1 to "S", 2 to "P", 3 to "A", 4 to "L", 5 to "L+"
)