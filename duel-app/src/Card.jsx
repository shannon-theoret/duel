import ageOneBack from './img/ageOneBack.jpg';
import ageTwoBack from './img/ageTwoBack.jpg';
import ageThreeBack from './img/ageThreeBack.jpg';
import guildBack from './img/guildBack.jpg';
import altar from './img/altar.jpg'
import apothecary from './img/apothecary.jpg';
import baths from './img/baths.jpg';
import clayPit from './img/clayPit.jpg';
import clayPool from './img/clayPool.jpg';
import clayReserve from './img/clayReserve.jpg';
import garrison from './img/garrison.jpg';
import glassworks from './img/glassworks.jpg';
import guardTower from './img/guardTower.jpg';
import loggingCamp from './img/loggingCamp.jpg';
import lumbarYard from './img/lumbarYard.jpg';
import palisade from './img/palisade.jpg';
import pharmacist from './img/pharmacist.jpg';
import press from './img/press.jpg';
import quarry from './img/quarry.jpg';
import scriptorium from './img/scriptorium.jpg';
import stable from './img/stable.jpg';
import stonePit from './img/stonePit.jpg';
import stoneReserve from './img/stoneReserve.jpg';
import tavern from './img/tavern.jpg';
import theatre from './img/theatre.jpg';
import woodReserve from './img/woodReserve.jpg';
import workshop from './img/workshop.jpg';
import aqueduct from './img/2_aqueduct.jpg';
import archeryRange from './img/2_archeryRange.jpg';
import barracks from './img/2_barracks.jpg';
import brewery from './img/2_brewery.jpg';
import brickyard from './img/2_brickyard.jpg';
import caravansery from './img/2_caravansery.jpg';
import courthouse from './img/2_courthouse.jpg';
import customsHouse from './img/2_customsHouse.jpg';
import dispensary from './img/2_dispensary.jpg';
import dryingRoom from './img/2_dryingRoom.jpg';
import forum from './img/2_forum.jpg';
import glassblower from './img/2_glassblower.jpg';
import horseBreeders from './img/2_horseBreeders.jpg';
import laboratory from './img/2_laboratory.jpg';
import library from './img/2_library.jpg';
import paradeGround from './img/2_paradeGround.jpg';
import postrum from './img/2_postrum.jpg';
import sawmill from './img/2_sawmill.jpg';
import school from './img/2_school.jpg';
import shelfQuarry from './img/2_shelfQuarry.jpg';
import statue from './img/2_statue.jpg';
import temple from './img/2_temple.jpg';
import walls from './img/2_walls.jpg';
import academy from './img/3_academy.jpg';
import arena from './img/3_arena.jpg';
import armory from './img/3_armory.jpg';
import arsenal from './img/3_arsenal.jpg';
import chamberOfCommerce from './img/3_chamberOfCommerce.jpg';
import circus from './img/3_circus.jpg';
import fortifications from './img/3_fortifications.jpg';
import gardens from './img/3_gardens.jpg';
import lighthouse from './img/3_lighthouse.jpg';
import obelisk from './img/3_obelisk.jpg';
import observatory from './img/3_observatory.jpg';
import palace from './img/3_palace.jpg';
import pantheon from './img/3_pantheon.jpg';
import port from './img/3_port.jpg';
import pretorium from './img/3_pretorium.jpg';
import senate from './img/3_senate.jpg';
import siegeWorkshop from './img/3_siegeWorkshop.jpg';
import study from './img/3_study.jpg';
import townhall from './img/3_townhall.jpg';
import university from './img/3_university.jpg';
import buildersGuild from './img/g_buildersGuild.jpg';
import magistratesGuild from './img/g_magistratesGuild.jpg';
import merchantsGuild from './img/g_merchantsGuild.jpg';
import moneylendersGuild from './img/g_moneylendersGuild.jpg';
import scientistsGuild from './img/g_scientistsGuild.jpg';
import shipownersGuild from './img/g_shipownersGuild.jpg';
import tacticiansGuild from './img/g_tacticiansGuild.jpg';
import Tooltip from './Tooltip';

export default function Card({cName, cardName, isActive, cardSetter, index, selected, otherOnClick}) {
    const imgMap = {
        "AGE_ONE_BACK": <img src={ageOneBack}/>,
        "AGE_TWO_BACK": <img src={ageTwoBack} />,
        "AGE_THREE_BACK": <img src={ageThreeBack} />,
        "GUILD_BACK": <img src={guildBack} />,
        "ALTER": <img src={altar}/>,
        "APOTHECARY": <img src={apothecary}/>,
        "BATHS": <img src={baths}/>,
        "CLAY_PIT": <img src={clayPit}/>,
        "CLAY_POOL": <img src={clayPool}/>,
        "CLAY_RESERVE": <img src={clayReserve}/>,
        "GARRISON": <img src={garrison}/>,
        "GLASSWORKS": <img src={glassworks}/>,
        "GUARD_TOWER": <img src={guardTower}/>,
        "LOGGING_CAMP": <img src={loggingCamp}/>,
        "LUMBER_YARD": <img src={lumbarYard}/>,
        "PALISADE": <img src={palisade}/>,
        "PHARMACIST": <img src={pharmacist}/>,
        "PRESS": <img src={press}/>,
        "QUARRY": <img src={quarry}/>,
        "SCRIPTORIUM": <img src={scriptorium}/>,
        "STABLE": <img src={stable}/>,
        "STONE_PIT": <img src={stonePit}/>,
        "STONE_RESERVE": <img src={stoneReserve}/>,
        "TAVERN": <img src={tavern}/>,
        "THEATRE": <img src={theatre}/>,
        "WOOD_RESERVE": <img src={woodReserve}/>,
        "WORKSHOP": <img src={workshop}/>,
        "AQUEDUCT": <img src={aqueduct} />,
        "ARCHERY_RANGE": <img src={archeryRange} />,
        "BARRACKS": <img src={barracks} />,
        "BREWERY": <img src={brewery} />,
        "BRICKYARD": <img src={brickyard} />,
        "CARAVANSERY": <img src={caravansery} />,
        "COURTHOUSE": <img src={courthouse} />,
        "CUSTOMS_HOUSE": <img src={customsHouse} />,
        "DISPENSARY": <img src={dispensary} />,
        "DRYING_ROOM": <img src={dryingRoom} />,
        "FORUM": <img src={forum} />,
        "GLASSBLOWER": <img src={glassblower} />,
        "HORSE_BREEDERS": <img src={horseBreeders} />,
        "LABORATORY": <img src={laboratory} />,
        "LIBRARY": <img src={library} />,
        "PARADE_GROUND": <img src={paradeGround} />,
        "POSTRUM": <img src={postrum} />,
        "SAWMILL": <img src={sawmill} />,
        "SCHOOL": <img src={school} />,
        "SHELF_QUARRY": <img src={shelfQuarry} />,
        "STATUE": <img src={statue} />,
        "TEMPLE": <img src={temple} />,
        "WALLS": <img src={walls} />,
        "ACADEMY": <img src={academy} />,
        "ARENA": <img src={arena} />,
        "ARMORY": <img src={armory} />,
        "ARSENAL": <img src={arsenal} />,
        "CHAMBER_OF_COMMERCE": <img src={chamberOfCommerce} />,
        "CIRCUS": <img src={circus} />,
        "FORTIFICATIONS": <img src={fortifications} />,
        "GARDENS": <img src={gardens} />,
        "LIGHTHOUSE": <img src={lighthouse} />,
        "OBELISK": <img src={obelisk} />,
        "OBSERVATORY": <img src={observatory} />,
        "PALACE": <img src={palace} />,
        "PANTHEON": <img src={pantheon} />,
        "PORT": <img src={port} />,
        "PRETORIUM": <img src={pretorium} />,
        "SENATE": <img src={senate} />,
        "SIEGE_WORKSHOP": <img src={siegeWorkshop} />,
        "STUDY": <img src={study} />,
        "TOWNHALL": <img src={townhall} />,
        "UNIVERSITY": <img src={university} />,
        "BUILDERS_GUILD": <img src={buildersGuild} />,
        "MAGISTRATES_GUILD": <img src={magistratesGuild} />,
        "MERCHANTS_GUILD": <img src={merchantsGuild} />,
        "MONEYLENDERS_GUILD": <img src={moneylendersGuild} />,
        "SCIENTISTS_GUILD": <img src={scientistsGuild} />,
        "SHIPOWNERS_GUILD": <img src={shipownersGuild} />,
        "TACTICIANS_GUILD": <img src={tacticiansGuild} />
    }

    return <span className={cName + (selected ? " sel" : "")} onClick={isActive ? () => cardSetter(index) : otherOnClick ? () => otherOnClick(cardName) : null}>{imgMap[cardName]}</span>;
}