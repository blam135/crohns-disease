from scripts.XMLBuilder import output_to_file, setupXML, param_global

def main():
    for i in range(-20, 21, 5):
        param = param_global.copy()
        param["Input"]["Simulation"]["numFOXP3TregCell"] = str(30 + i)
        param["Input"]["Simulation"]["numTh1Cell"] = str(30 - i)
        param["Input"]["Simulation"]["numDendriticCell"] = "30"
        param["Input"]["Simulation"]["numIntestinalEpithelialCell"] = "500"

        # Setup Th1 Cell
        param["Input"]["Th1Cell"]["TNFSecretedPerHour"] = "20"

        # Setup FOXP3Treg Cell
        param["Input"]["FOXP3TregCell"]["IL35SecretedPerHour"] = "20"
        param["Input"]["FOXP3TregCell"]["TGFBSecretedPerHour"] = "20"
        param["Input"]["FOXP3TregCell"]["IL10SecretedPerHour"] = "20"
        param["Input"]["FOXP3TregCell"]["IL33ActivationThreshold"] = "1"

        # Setup IECs
        param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["Il1BActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["IL18ActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["TNFActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["TNFActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IL22ActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IL17ActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IFNyActivationThreshold"] = "1"

        xml = setupXML(param)
        output_to_file(xml, "../../resources/FOXP3TregandTh1CellsDiff{0}.xml".format(i))

if __name__ == '__main__':
    main()