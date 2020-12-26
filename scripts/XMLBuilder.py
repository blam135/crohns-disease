import xml.etree.cElementTree as ET
from xml.dom import minidom

default_secretion_value = "0"
default_activation_threshold = "100000" # Impossible to activate
# default_activation_threshold = "1" # Gotta change this...
timeSlice = "0.125"

param_global = {
    "Input": {
        "Simulation": {
            "timeSlice": timeSlice,
            "numMicrobe": "0",
            "numDendriticCell": "0",
            "numIntestinalEpithelialCell": "0",
            "numIntraEpithelialCell": "0",
            "numMacrophage": "0",
            "numNaiveTCell": "0",
            "numTh1Cell": "0",
            "numTh17Cell":  "0",
            "numFOXP3TregCell": "0",
            "numDummyCell": "0"
        },
        "Phagocyte": {
            "probOfPhagocytosis": "1"
        },
        "Molecule": {
            "halflife": "0.5",
            "decayThreshold": "0.01"
        },
        "Epithelium": {
            "height": "100",
            "width": "100",
            "timeToCrossOrgan": "5.0"
        },
        "LymphNode": {
            "height": "50",
            "width": "50",
            "timeToCrossOrgan": "5.0"
        },
        "LaminaPropria": {
            "height": "100",
            "width": "100",
            "timeToCrossOrgan": "5.0"
        },
        "DendriticCell": {
            "NonTolerogenic": {
                "Th1Priming": {
                    "IL12SecretedPerHour": default_secretion_value
                },
                "Th17Priming": {
                    "IL6SecretedPerHour": default_secretion_value,
                    "IL23SecretedPerHour": default_secretion_value,
                    "IL1BSecretedPerHour": default_secretion_value,
                    "TGFBSecretedPerHour": default_secretion_value,
                }
            },
            "Tolegeneric": {
                "FOXP3Priming": {
                    "TGFBSecretedPerHour": default_secretion_value
                },
                "Th17Priming": {
                    "IL6SecretedPerHour": default_secretion_value,
                    "IL23SecretedPerHour": default_secretion_value,
                    "IL1BSecretedPerHour": default_secretion_value,
                    "TGFBSecretedPerHour": default_secretion_value,
                }
            },
            "timeOfDeathMean": "110.0",
            "timeOfDeathStdDev": "48.0",
            "probabilityOfNonTolerogenic": "0.9",
        },
        "NaiveTCell": {
            "IL12ActivationThreshold": default_activation_threshold,
            "IL6ActivationThreshold": default_activation_threshold,
            "IL23ActivationThreshold": default_activation_threshold,
            "TGFBActivationThreshold": default_activation_threshold,
            "IL1BActivationThreshold": default_activation_threshold,
        },
        "Th1Cell": {
            "IFNySecretedPerHour": default_secretion_value,
            "TNFSecretedPerHour": default_secretion_value,
            "IL2SecretedPerHour": default_secretion_value,
            "timeOfDeathMean": "110.0",
            "timeOfDeathStdDev": "48.0",
            "proliferationMean": "19.2",
            "proliferationStdDev": "9.6"
        },
        "Th17Cell": {
            "IL17SecretedPerHour": default_secretion_value,
            "IL22SecretedPerHour": default_secretion_value,
            "timeOfDeathMean": "110.0",
            "timeOfDeathStdDev": "48.0",
            "proliferationMean": "19.2",
            "proliferationStdDev": "9.6"
        },
        "FOXP3TregCell": {
            "IL35SecretedPerHour": default_secretion_value,
            "TGFBSecretedPerHour": default_secretion_value,
            "IL10SecretedPerHour": default_secretion_value,
            "IL33ActivationThreshold": default_activation_threshold,
            "timeOfDeathMean": "110.0",
            "timeOfDeathStdDev": "48.0",
            "proliferationMean": "19.2",
            "proliferationStdDev": "9.6"
        },
        "Macrophage": {
            "IFNyActivationThreshold": default_activation_threshold,
            "IL10ActivationThreshold": default_activation_threshold,
            "IL17ActivationThreshold": default_activation_threshold,
            "GMCSFActivationThreshold": default_activation_threshold,
            "IL23SecretedPerHour": default_secretion_value,
            "TNFSecretedPerHour": default_secretion_value,
            "IL6SecretedPerHour": default_secretion_value,
            "IL1BSecretedPerHour": default_secretion_value,
            "IL18SecretedPerHour": default_secretion_value,
            "IL12SecretedPerHour": default_secretion_value,
            "IL10SecretedPerHour": default_secretion_value,
            "TGFBSecretedPerHour": default_secretion_value
        },
        "ILC3": {
            "NKp44Positive": {
                "IL22SecretedPerHour": default_secretion_value
            },
            "NKp44Negative": {
                "GMCSFSecretedPerHour": default_secretion_value,
                "IL17SecretedPerHour": default_secretion_value,
                "IL22SecretedPerHour": default_secretion_value,
            },
            "IL23ActivationThreshold": default_activation_threshold,
            "IL1BActivationThreshold": default_activation_threshold,
            "IL2ActivationThreshold": default_activation_threshold,
            "IL7ActivationThreshold": default_activation_threshold,
            "amountWhenSpawned": "50",
        },
        "IntestinalEpithelialCell": {
            "Homeostasis": {
                "IL33SecretedPerHour": default_secretion_value,
                "IL17ActivationThreshold": default_activation_threshold,
                "antiMicrobialPeptideSecretedPerHour": default_secretion_value,
                "IL15SecretedPerHour": default_secretion_value,
                "IL7SecretedPerHour": default_secretion_value,
                "IL10ActivationThreshold": default_activation_threshold,
                "TGFBActivationThreshold": default_activation_threshold,
                "MucusSecretion": {
                    "IL6ActivationThreshold": default_activation_threshold,
                    "IL22ActivationThreshold": default_activation_threshold,
                    "IL18BlockageThreshold": "1",
                    "MucusSecretedPerHour": default_secretion_value
                },
                "Proliferation": {
                    "proliferationMean": "19.2",
                    "proliferationStdDev": "9.6",
                    "IL10ActivationThreshold": default_activation_threshold,
                    "IL22ActivationThreshold": default_activation_threshold,
                    "IL17ActivationThreshold": default_activation_threshold,
                    "IL6ActivationThreshold": default_activation_threshold
                },
            },
            "Inflammation": {
                "Il1BActivationThreshold": default_activation_threshold,
                "IL18ActivationThreshold": default_activation_threshold,
                "TNFActivationThreshold": default_activation_threshold,
                "TNFSecretedPerHour": default_secretion_value
            },
            "Apoptosis": {
                "TNFActivationThreshold": default_activation_threshold,
                "IL17ActivationThreshold": default_activation_threshold,
                "IFNyActivationThreshold": default_activation_threshold
            },
        },
        "IntraepithelialCell": {
            "IL17SecretedPerHour": default_secretion_value,
            "IL6SecretedPerHour": default_secretion_value,
            "IL22SecretedPerHour": default_secretion_value,
            "TGFBSecretedPerHour": default_secretion_value,
            "IL15ActivationThreshold": default_activation_threshold,
            "timeOfDeathMean": "110.0",
            "timeOfDeathStdDev": "10",
            "proliferationTimeMean": "48.0",
            "proliferationTimeStdDev": "10"
        }
    }
}

# https://stackoverflow.com/questions/17402323/use-xml-etree-elementtree-to-print-nicely-formatted-xml-files/17402424
def output_to_file(elem, file_name):
    rough_string = ET.tostring(elem, 'utf-8')
    reparsed = minidom.parseString(rough_string)
    text = reparsed.toprettyxml(indent="\t")
    file = open(file_name, "w")
    file.write(text)
    file.close()

def simulationTag(root, param):
    sim = ET.SubElement(root, "Simulation")
    ET.SubElement(sim, "timeSlice").text = param["Input"]["Simulation"]["timeSlice"]
    ET.SubElement(sim, "numMicrobe").text = param["Input"]["Simulation"]["numMicrobe"]
    ET.SubElement(sim, "numDendriticCell").text = param["Input"]["Simulation"]["numDendriticCell"]
    ET.SubElement(sim, "numIntestinalEpithelialCell").text = param["Input"]["Simulation"]["numIntestinalEpithelialCell"]
    ET.SubElement(sim, "numIntraEpithelialCell").text = param["Input"]["Simulation"]["numIntraEpithelialCell"]
    ET.SubElement(sim, "numMacrophage").text = param["Input"]["Simulation"]["numMacrophage"]
    ET.SubElement(sim, "numNaiveTCell").text = param["Input"]["Simulation"]["numNaiveTCell"]
    ET.SubElement(sim, "numTh1Cell").text = param["Input"]["Simulation"]["numTh1Cell"]
    ET.SubElement(sim, "numTh17Cell").text = param["Input"]["Simulation"]["numTh17Cell"]
    ET.SubElement(sim, "numFOXP3TregCell").text = param["Input"]["Simulation"]["numFOXP3TregCell"]
    ET.SubElement(sim, "numDummyCell").text = param["Input"]["Simulation"]["numDummyCell"]

def phagocyteTag(root, param):
    sim = ET.SubElement(root, "Phagocyte")
    ET.SubElement(sim, "probOfPhagocytosis").text = param["Input"]["Phagocyte"]["probOfPhagocytosis"]


def moleculeTag(root, param):
    sim = ET.SubElement(root, "Molecule")
    ET.SubElement(sim, "halflife").text = param["Input"]["Molecule"]["halflife"]
    ET.SubElement(sim, "decayThreshold").text = param["Input"]["Molecule"]["decayThreshold"]


def epitheliumTag(root, param):
    sim = ET.SubElement(root, "Epithelium")
    ET.SubElement(sim, "height").text = param["Input"]["Epithelium"]["height"]
    ET.SubElement(sim, "width").text = param["Input"]["Epithelium"]["width"]
    ET.SubElement(sim, "timeToCrossOrgan").text = param["Input"]["Epithelium"]["timeToCrossOrgan"]


def lymphNodeTag(root, param):
    sim = ET.SubElement(root, "LymphNode")
    ET.SubElement(sim, "height").text = param["Input"]["LymphNode"]["height"]
    ET.SubElement(sim, "width").text = param["Input"]["LymphNode"]["width"]
    ET.SubElement(sim, "timeToCrossOrgan").text = param["Input"]["LymphNode"]["timeToCrossOrgan"]


def laminaPropriaTag(root, param):
    sim = ET.SubElement(root, "LaminaPropria")
    ET.SubElement(sim, "height").text = param["Input"]["LaminaPropria"]["height"]
    ET.SubElement(sim, "width").text = param["Input"]["LaminaPropria"]["width"]
    ET.SubElement(sim, "timeToCrossOrgan").text = param["Input"]["LaminaPropria"]["timeToCrossOrgan"]

def dendriticCellTag(root, param):
    dc = ET.SubElement(root, "DendriticCell")

    nontol = ET.SubElement(dc, "NonTolerogenic")
    nontolTh1 = ET.SubElement(nontol, "Th1Priming")
    ET.SubElement(nontolTh1, "IL12SecretedPerHour").text = param["Input"]["DendriticCell"]["NonTolerogenic"]["Th1Priming"]["IL12SecretedPerHour"]
    nontolTh17 = ET.SubElement(nontol, "Th17Priming")
    ET.SubElement(nontolTh17, "IL6SecretedPerHour").text = param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL6SecretedPerHour"]
    ET.SubElement(nontolTh17, "IL23SecretedPerHour").text = param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL23SecretedPerHour"]
    ET.SubElement(nontolTh17, "IL1BSecretedPerHour").text = param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL1BSecretedPerHour"]
    ET.SubElement(nontolTh17, "TGFBSecretedPerHour").text = param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["TGFBSecretedPerHour"]

    tol = ET.SubElement(dc, "Tolegeneric")
    tolfoxp3 = ET.SubElement(tol, "FOXP3Priming")
    ET.SubElement(tolfoxp3, "TGFBSecretedPerHour").text = param["Input"]["DendriticCell"]["Tolegeneric"]["FOXP3Priming"]["TGFBSecretedPerHour"]
    tolTh17 = ET.SubElement(tol, "Th17Priming")
    ET.SubElement(tolTh17, "IL6SecretedPerHour").text = param["Input"]["DendriticCell"]["Tolegeneric"]["Th17Priming"]["IL6SecretedPerHour"]
    ET.SubElement(tolTh17, "IL23SecretedPerHour").text = param["Input"]["DendriticCell"]["Tolegeneric"]["Th17Priming"]["IL23SecretedPerHour"]
    ET.SubElement(tolTh17, "IL1BSecretedPerHour").text = param["Input"]["DendriticCell"]["Tolegeneric"]["Th17Priming"]["IL1BSecretedPerHour"]
    ET.SubElement(tolTh17, "TGFBSecretedPerHour").text = param["Input"]["DendriticCell"]["Tolegeneric"]["Th17Priming"]["TGFBSecretedPerHour"]

    ET.SubElement(dc, "timeOfDeathMean").text = param["Input"]["DendriticCell"]["timeOfDeathMean"]
    ET.SubElement(dc, "timeOfDeathStdDev").text = param["Input"]["DendriticCell"]["timeOfDeathStdDev"]
    ET.SubElement(dc, "probabilityOfNonTolerogenic").text = param["Input"]["DendriticCell"]["probabilityOfNonTolerogenic"]

def naiveTCellTag(root, param):
    sim = ET.SubElement(root, "NaiveTCell")
    ET.SubElement(sim, "IL12ActivationThreshold").text = param["Input"]["NaiveTCell"]["IL12ActivationThreshold"]
    ET.SubElement(sim, "IL6ActivationThreshold").text = param["Input"]["NaiveTCell"]["IL6ActivationThreshold"]
    ET.SubElement(sim, "IL23ActivationThreshold").text = param["Input"]["NaiveTCell"]["IL23ActivationThreshold"]
    ET.SubElement(sim, "TGFBActivationThreshold").text = param["Input"]["NaiveTCell"]["TGFBActivationThreshold"]
    ET.SubElement(sim, "IL1BActivationThreshold").text = param["Input"]["NaiveTCell"]["IL1BActivationThreshold"]

def th1CellTag(root, param):
    sim = ET.SubElement(root, "Th1Cell")
    ET.SubElement(sim, "IFNySecretedPerHour").text = param["Input"]["Th1Cell"]["IFNySecretedPerHour"]
    ET.SubElement(sim, "TNFSecretedPerHour").text = param["Input"]["Th1Cell"]["TNFSecretedPerHour"]
    ET.SubElement(sim, "IL2SecretedPerHour").text = param["Input"]["Th1Cell"]["IL2SecretedPerHour"]
    ET.SubElement(sim, "timeOfDeathMean").text = param["Input"]["Th1Cell"]["timeOfDeathMean"]
    ET.SubElement(sim, "timeOfDeathStdDev").text = param["Input"]["Th1Cell"]["timeOfDeathStdDev"]
    ET.SubElement(sim, "proliferationMean").text = param["Input"]["Th1Cell"]["proliferationMean"]
    ET.SubElement(sim, "proliferationStdDev").text = param["Input"]["Th1Cell"]["proliferationStdDev"]

def th17CellTag(root, param):
    sim = ET.SubElement(root, "Th17Cell")
    ET.SubElement(sim, "IL17SecretedPerHour").text = param["Input"]["Th17Cell"]["IL17SecretedPerHour"]
    ET.SubElement(sim, "IL22SecretedPerHour").text = param["Input"]["Th17Cell"]["IL22SecretedPerHour"]
    ET.SubElement(sim, "timeOfDeathMean").text = param["Input"]["Th17Cell"]["timeOfDeathMean"]
    ET.SubElement(sim, "timeOfDeathStdDev").text = param["Input"]["Th17Cell"]["timeOfDeathStdDev"]
    ET.SubElement(sim, "proliferationMean").text = param["Input"]["Th17Cell"]["proliferationMean"]
    ET.SubElement(sim, "proliferationStdDev").text = param["Input"]["Th17Cell"]["proliferationStdDev"]

def foxP3TregCellTag(root, param):
    sim = ET.SubElement(root, "FOXP3TregCell")
    ET.SubElement(sim, "IL35SecretedPerHour").text = param["Input"]["FOXP3TregCell"]["IL35SecretedPerHour"]
    ET.SubElement(sim, "TGFBSecretedPerHour").text = param["Input"]["FOXP3TregCell"]["TGFBSecretedPerHour"]
    ET.SubElement(sim, "IL10SecretedPerHour").text = param["Input"]["FOXP3TregCell"]["IL10SecretedPerHour"]
    ET.SubElement(sim, "IL33ActivationThreshold").text = param["Input"]["FOXP3TregCell"]["IL33ActivationThreshold"]
    ET.SubElement(sim, "timeOfDeathMean").text = param["Input"]["FOXP3TregCell"]["timeOfDeathMean"]
    ET.SubElement(sim, "timeOfDeathStdDev").text = param["Input"]["FOXP3TregCell"]["timeOfDeathStdDev"]
    ET.SubElement(sim, "proliferationMean").text = param["Input"]["FOXP3TregCell"]["proliferationMean"]
    ET.SubElement(sim, "proliferationStdDev").text = param["Input"]["FOXP3TregCell"]["proliferationStdDev"]

def macrophageTag(root, param):
    sim = ET.SubElement(root, "Macrophage")
    ET.SubElement(sim, "IFNyActivationThreshold").text = param["Input"]["Macrophage"]["IFNyActivationThreshold"]
    ET.SubElement(sim, "IL10ActivationThreshold").text = param["Input"]["Macrophage"]["IL10ActivationThreshold"]
    ET.SubElement(sim, "IL17ActivationThreshold").text = param["Input"]["Macrophage"]["IL17ActivationThreshold"]
    ET.SubElement(sim, "GMCSFActivationThreshold").text = param["Input"]["Macrophage"]["GMCSFActivationThreshold"]
    ET.SubElement(sim, "IL23SecretedPerHour").text = param["Input"]["Macrophage"]["IL23SecretedPerHour"]
    ET.SubElement(sim, "TNFSecretedPerHour").text = param["Input"]["Macrophage"]["TNFSecretedPerHour"]
    ET.SubElement(sim, "IL6SecretedPerHour").text = param["Input"]["Macrophage"]["IL6SecretedPerHour"]
    ET.SubElement(sim, "IL1BSecretedPerHour").text = param["Input"]["Macrophage"]["IL1BSecretedPerHour"]
    ET.SubElement(sim, "IL18SecretedPerHour").text = param["Input"]["Macrophage"]["IL18SecretedPerHour"]
    ET.SubElement(sim, "IL12SecretedPerHour").text = param["Input"]["Macrophage"]["IL12SecretedPerHour"]
    ET.SubElement(sim, "IL10SecretedPerHour").text = param["Input"]["Macrophage"]["IL10SecretedPerHour"]
    ET.SubElement(sim, "TGFBSecretedPerHour").text = param["Input"]["Macrophage"]["TGFBSecretedPerHour"]

def ilc3Tag(root, param):
    sim = ET.SubElement(root, "ILC3")
    positive = ET.SubElement(sim, "NKp44Positive")
    ET.SubElement(positive, "IL22SecretedPerHour").text = param["Input"]["ILC3"]["NKp44Positive"]["IL22SecretedPerHour"]
    negative = ET.SubElement(sim, "NKp44Negative")
    ET.SubElement(negative, "GMCSFSecretedPerHour").text = param["Input"]["ILC3"]["NKp44Negative"]["GMCSFSecretedPerHour"]
    ET.SubElement(negative, "IL17SecretedPerHour").text = param["Input"]["ILC3"]["NKp44Negative"]["IL17SecretedPerHour"]
    ET.SubElement(negative, "IL22SecretedPerHour").text = param["Input"]["ILC3"]["NKp44Negative"]["IL22SecretedPerHour"]

    ET.SubElement(sim, "IL23ActivationThreshold").text = param["Input"]["ILC3"]["IL23ActivationThreshold"]
    ET.SubElement(sim, "IL1BActivationThreshold").text = param["Input"]["ILC3"]["IL1BActivationThreshold"]
    ET.SubElement(sim, "IL2ActivationThreshold").text = param["Input"]["ILC3"]["IL2ActivationThreshold"]
    ET.SubElement(sim, "IL7ActivationThreshold").text = param["Input"]["ILC3"]["IL7ActivationThreshold"]
    ET.SubElement(sim, "amountWhenSpawned").text = param["Input"]["ILC3"]["amountWhenSpawned"]


def iecTag(root, param):
    sim = ET.SubElement(root, "IntestinalEpithelialCell")

    homeo = ET.SubElement(sim, "Homeostasis")
    ET.SubElement(homeo, "IL33SecretedPerHour").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["IL33SecretedPerHour"]
    ET.SubElement(homeo, "IL17ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["IL17ActivationThreshold"]
    ET.SubElement(homeo, "antiMicrobialPeptideSecretedPerHour").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["antiMicrobialPeptideSecretedPerHour"]
    ET.SubElement(homeo, "IL15SecretedPerHour").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["IL15SecretedPerHour"]
    ET.SubElement(homeo, "IL7SecretedPerHour").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["IL7SecretedPerHour"]
    ET.SubElement(homeo, "IL10ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["IL10ActivationThreshold"]
    ET.SubElement(homeo, "TGFBActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["TGFBActivationThreshold"]
    homeomucus = ET.SubElement(homeo, "MucusSecretion")
    ET.SubElement(homeomucus, "IL6ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["MucusSecretion"]["IL6ActivationThreshold"]
    ET.SubElement(homeomucus, "IL22ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["MucusSecretion"]["IL22ActivationThreshold"]
    ET.SubElement(homeomucus, "IL18BlockageThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["MucusSecretion"]["IL18BlockageThreshold"]
    ET.SubElement(homeomucus, "MucusSecretedPerHour").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["MucusSecretion"]["MucusSecretedPerHour"]
    homeoprolif = ET.SubElement(homeo, "Proliferation")
    ET.SubElement(homeoprolif, "proliferationMean").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["Proliferation"]["proliferationMean"]
    ET.SubElement(homeoprolif, "proliferationStdDev").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["Proliferation"]["proliferationStdDev"]
    ET.SubElement(homeoprolif, "IL10ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["Proliferation"]["IL10ActivationThreshold"]
    ET.SubElement(homeoprolif, "IL22ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["Proliferation"]["IL22ActivationThreshold"]
    ET.SubElement(homeoprolif, "IL17ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["Proliferation"]["IL17ActivationThreshold"]
    ET.SubElement(homeoprolif, "IL6ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["Proliferation"]["IL6ActivationThreshold"]
    infla = ET.SubElement(sim, "Inflammation")
    ET.SubElement(infla, "Il1BActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["Il1BActivationThreshold"]
    ET.SubElement(infla, "IL18ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["IL18ActivationThreshold"]
    ET.SubElement(infla, "TNFActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["TNFActivationThreshold"]
    ET.SubElement(infla, "TNFSecretedPerHour").text = param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["TNFSecretedPerHour"]
    apop = ET.SubElement(sim, "Apoptosis")
    ET.SubElement(apop, "TNFActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["TNFActivationThreshold"]
    ET.SubElement(apop, "IL17ActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IL17ActivationThreshold"]
    ET.SubElement(apop, "IFNyActivationThreshold").text = param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IFNyActivationThreshold"]

def ielTag(root, param):
    sim = ET.SubElement(root, "IntraepithelialCell")
    ET.SubElement(sim, "IL17SecretedPerHour").text = param["Input"]["IntraepithelialCell"]["IL17SecretedPerHour"]
    ET.SubElement(sim, "IL6SecretedPerHour").text = param["Input"]["IntraepithelialCell"]["IL6SecretedPerHour"]
    ET.SubElement(sim, "IL22SecretedPerHour").text = param["Input"]["IntraepithelialCell"]["IL22SecretedPerHour"]
    ET.SubElement(sim, "TGFBSecretedPerHour").text = param["Input"]["IntraepithelialCell"]["TGFBSecretedPerHour"]
    ET.SubElement(sim, "IL15ActivationThreshold").text = param["Input"]["IntraepithelialCell"]["IL15ActivationThreshold"]
    ET.SubElement(sim, "timeOfDeathMean").text = param["Input"]["IntraepithelialCell"]["timeOfDeathMean"]
    ET.SubElement(sim, "timeOfDeathStdDev").text = param["Input"]["IntraepithelialCell"]["timeOfDeathStdDev"]
    ET.SubElement(sim, "proliferationTimeMean").text = param["Input"]["IntraepithelialCell"]["proliferationTimeMean"]
    ET.SubElement(sim, "proliferationTimeStdDev").text = param["Input"]["IntraepithelialCell"]["proliferationTimeStdDev"]

# Returns the root tag
def setupXML(param):
    root = ET.Element("Input")
    simulationTag(root, param)
    phagocyteTag(root, param)
    moleculeTag(root, param)
    epitheliumTag(root, param)
    lymphNodeTag(root, param)
    laminaPropriaTag(root, param)
    dendriticCellTag(root, param)
    naiveTCellTag(root, param)
    th1CellTag(root, param)
    th17CellTag(root, param)
    foxP3TregCellTag(root, param)
    macrophageTag(root, param)
    ilc3Tag(root, param)
    iecTag(root, param)
    ielTag(root, param)
    return root
