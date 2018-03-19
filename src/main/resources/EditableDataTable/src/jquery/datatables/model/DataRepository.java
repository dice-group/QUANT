package jquery.datatables.model;

import java.util.LinkedList;
import java.util.List;

public class DataRepository {
	
	/// <summary>
    /// Singleton collection of companies
    /// </summary>
    private static List<Company> CompanyData = null;

    /// <summary>
    /// Method that returns all companies used in this example
    /// </summary>
    /// <returns>List of companies</returns>
    public static List<Company> GetCompanies()
    {
        if (CompanyData == null)
        {
            CompanyData = new LinkedList<Company>();
            CompanyData.add(new Company("Emkay Entertainments", "Nobel House, Regent Centre", "Lothian" ));
            CompanyData.add(new Company("The Empire", "Milton Keynes Leisure Plaza", "Buckinghamshire" ));
            CompanyData.add(new Company("Asadul Ltd", "Hophouse", "Essex" ));
            CompanyData.add(new Company("Gargamel ltd", "", "" ));
            CompanyData.add(new Company("Ashley Mark Publishing Company", "1-2 Vance Court", "Tyne & Wear" ));
            CompanyData.add(new Company("MuchMoreMusic Studios", "Unit 29", "London" ));
            CompanyData.add(new Company("Victoria Music Ltd", "Unit 215", "London" ));
            CompanyData.add(new Company("Abacus Agent", "Regent Street", "London" ));
            CompanyData.add(new Company("Atomic", "133 Longacre", "London" ));
            CompanyData.add(new Company("Pyramid Posters", "The Works", "Leicester" ));
            CompanyData.add(new Company("Kingston Smith Financial Services Ltd", "105 St Peter's Street", "Herts" ));
            CompanyData.add(new Company("Garrett Axford PR", "Harbour House", "West Sussex" ));
            CompanyData.add(new Company("Derek Boulton Management", "76 Carlisle Mansions", "London" ));
            CompanyData.add(new Company("Total Concept Management (TCM)", "PO Box 128", "West Yorks" ));
            CompanyData.add(new Company("Billy Russell Management", "Binny Estate", "Edinburgh" ));
            CompanyData.add(new Company("Stage Audio Services", "Unit 2", "Stourbridge" ));
            CompanyData.add(new Company("Windsong International", "Heather Court", "Kent" ));
            CompanyData.add(new Company("Vivante Music Ltd", "32 The Netherlands", "Surrey" ));
            CompanyData.add(new Company("Way to Blue", "First Floor", "London" ));
            CompanyData.add(new Company("Glasgow City Halls", "32 Albion Street", "Lanarkshire" ));
            CompanyData.add(new Company("The List", "14 High St", "Edinburgh" ));
            CompanyData.add(new Company("Wilkinson Turner King", "10A London Road", "Cheshire" ));
            CompanyData.add(new Company("GSC Solicitors", "31-32 Ely Place", "London" ));
            CompanyData.add(new Company("Vanessa Music Co", "35 Tower Way", "Devon" ));
            CompanyData.add(new Company("Regent Records", "PO Box 528", "West Midlands" ));
            CompanyData.add(new Company("BBC Radio Lancashire", "20-26 Darwen St", "Blackburn" ));
            CompanyData.add(new Company("The Citadel Arts Centre", "Waterloo Street", "Merseyside" ));
            CompanyData.add(new Company("Villa Audio Ltd", "Baileys Yard", "Essex" ));
            CompanyData.add(new Company("Astra travel", "", "" ));
            CompanyData.add(new Company("Idle Eyes Printshop", "81 Sheen Court", "Surrey" ));
            CompanyData.add(new Company("Miggins Music (UK)", "33 Mandarin Place", "Oxon" ));
            CompanyData.add(new Company("Magic 999", "St Paul's Square", "Lancashire" ));
            CompanyData.add(new Company("Delga Group", "Seaplane House, Riverside Est.", "Kent" ));
            CompanyData.add(new Company("Zane Music", "162 Castle Hill", "Berkshire" ));
            CompanyData.add(new Company("Universal Music Operations", "Chippenham Drive", "Milton Keynes" ));
            CompanyData.add(new Company("Gotham Records", "PO Box 6003", "Birmingham" ));
            CompanyData.add(new Company("Timbuktu Music Ltd", "99C Talbot Road", "London" ));
            CompanyData.add(new Company("Online Music", "Unit 18, Croydon House", "Surrey" ));
            CompanyData.add(new Company("Irish Music Magazine", "11 Clare St", "Ireland" ));
            CompanyData.add(new Company("Savoy Records", "PO Box 271", "Surrey" ));
            CompanyData.add(new Company("Temple Studios", "97A Kenilworth Road", "Middlesex" ));
            CompanyData.add(new Company("Gravity Shack Studio", "Unit 3 ", "London" ));
            CompanyData.add(new Company("Dovehouse Records", "Crabtree Cottage", "Oxon" ));
            CompanyData.add(new Company("Citysounds Ltd", "5 Kirby Street", "London" ));
            CompanyData.add(new Company("Revolver Music Publishing", "152 Goldthorn Hill", "West Midlands" ));
            CompanyData.add(new Company("Jug Of Ale", "43 Alcester Road", "West Midlands" ));
            CompanyData.add(new Company("Isles FM 103", "PO Box 333", "Western Isles" ));
            CompanyData.add(new Company("Headscope", "Headrest", "East Sussex" ));
            CompanyData.add(new Company("Universal Music Ireland", "9 Whitefriars", "Ireland" ));
            CompanyData.add(new Company("Zander Exports", "34 Sapcote Trading Centre", "London" ));
            CompanyData.add(new Company("Midem (UK)", "Walmar House", "London" ));
            CompanyData.add(new Company("La Rocka Studios", "Post Mark House", "London" ));
            CompanyData.add(new Company("Warner Home DVD", "Warner House", "London" ));
            CompanyData.add(new Company("Music Room", "The Old Library", "London" ));
            CompanyData.add(new Company("Blue Planet", "96 York Street", "London" ));
            CompanyData.add(new Company("Dream 107.7FM", "Cater House", "Chelmsford" ));
            CompanyData.add(new Company("Moneypenny Agency", "The Stables, Westwood House", "East Yorks" ));
            CompanyData.add(new Company("Artsun", "18 Sparkle Street", "Manchester" ));
            CompanyData.add(new Company("Clyde 2", "Clydebank Business Park", "Glasgow" ));
            CompanyData.add(new Company("9PR", "65-69 White Lion Street", "London" ));
            CompanyData.add(new Company("River Studio's", "3 Grange Yard", "London" ));
            CompanyData.add(new Company("Start Entertainments Ltd", "3 Warmair House", "Middx" ));
            CompanyData.add(new Company("Vinyl Tap Mail Order Music", "1 Minerva Works", "West Yorkshire" ));
            CompanyData.add(new Company("Passion Music", "20 Blyth  Rd", "Middlesex" ));
            CompanyData.add(new Company("SuperVision Management", "Zeppelin Building", "London" ));
            CompanyData.add(new Company("Lite FM", "2nd Floor", "Peterborough" ));
            CompanyData.add(new Company("ISIS Duplicating Company", "Sales & Production", "Merseyside" ));
            CompanyData.add(new Company("Vanderbeek & Imrie Ltd", "15 Marvig", "Scotland" ));
            CompanyData.add(new Company("Glamorgan University", "Student Union", "Mid Glamorgan" ));
            CompanyData.add(new Company("Web User", "IPC Media", "London " ));
            CompanyData.add(new Company("Farnborough Recreation Centre", "1 Westmead", "Hampshire" ));
            CompanyData.add(new Company("Robert Owens/Musical Directions", "352A Kilburn Lane", "London" ));
            CompanyData.add(new Company("Magick Eye Records", "PO Box 3037", "Berks" ));
            CompanyData.add(new Company("Alexandra Theatre", "Station Street", "West Midlands" ));
            CompanyData.add(new Company("Keda Records", "The Sight And Sound Centre", "Middlesex" ));
            CompanyData.add(new Company("Independiente Ltd", "The Drill Hall", "London" ));
            CompanyData.add(new Company("Shurwood Management", "Tote Hill Cottage", "West Sussex" ));
            CompanyData.add(new Company("Fury Records", "PO Box 52", "Kent" ));
            CompanyData.add(new Company("Northumbria University", "Union Building", "Newcastle upon Tyne" ));
            CompanyData.add(new Company("Pop Muzik", "Haslemere", "W. Sussex" ));
            CompanyData.add(new Company("Jonsongs Music", "3 Farrers Place", "Surrey" ));
            CompanyData.add(new Company("Hermana PR", "Unit 244, Bon Marche Centre", "London" ));
            CompanyData.add(new Company("Sugarcane Music", "32 Blackmore Avenue", "Middlesex" ));
            CompanyData.add(new Company("JFM Records", "11 Alexander House", "London" ));
            CompanyData.add(new Company("Black Market Records", "25 D'Arblay Street", "London" ));
            CompanyData.add(new Company("Float Your Boat Productions", "5 Ralphs Retreat", "Bucks" ));
            CompanyData.add(new Company("Creation Management", "2 Berkley Grove", "London" ));
            CompanyData.add(new Company("Bryter Music", "Marlinspike Hall", "Suffolk" ));
            CompanyData.add(new Company("The Headline Agency", "39 Churchfields", "Ireland" ));
            CompanyData.add(new Company("MP Promotions", "13 Greave", "Cheshire" ));
            CompanyData.add(new Company("Modo Production Ltd", "Ground Floor", "London" ));
            CompanyData.add(new Company("Nomadic Music", "Unit 18", "London" ));
            CompanyData.add(new Company("Reverb Records Ltd", "Reverb House", "London" ));
            CompanyData.add(new Company("SIBC", "Market Street", "Lerwick" ));
            CompanyData.add(new Company("Marken Time Critical Express", "Unit 2", "Isleworth" ));
            CompanyData.add(new Company("102.2 Smooth FM", "26-27 Castlereagh Street", "London" ));
            CompanyData.add(new Company("Chesterfield Arts Centre", "Chesterfield College", "Derbyshire" ));
            CompanyData.add(new Company("The National Indoor Arena", "King Edward's Road", "West Midlands" ));
            CompanyData.add(new Company("Salisbury City Hall", "Malthouse Lane", "Wiltshire" ));
            CompanyData.add(new Company("Minder Music", "", "" ));
        }
        return CompanyData;
    }

}
