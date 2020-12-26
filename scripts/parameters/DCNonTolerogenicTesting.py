from scripts.XMLBuilder import param_global, output_to_file, setupXML

# Run on 200 time
def main():
    for i in range(0, 11, 1):
        param = param_global.copy()
        # Set up simulation
        param["Input"]["Simulation"]["numMicrobe"] = "500"
        param["Input"]["Simulation"]["numDendriticCell"] = "20"
        param["Input"]["Simulation"]["numIntestinalEpithelialCell"] = "1000"
        param["Input"]["Simulation"]["numMacrophage"] = "50"
        param["Input"]["Simulation"]["numNaiveTCell"] = "80"

        # Set up DC Priming
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th1Priming"]["IL12SecretedPerHour"] = "30"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL6SecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL23SecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL1BSecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["TGFBSecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["Tolegeneric"]["FOXP3Priming"]["TGFBSecretedPerHour"] = "30"
        param["Input"]["DendriticCell"]["Tolegeneric"]["Th17Priming"]["IL6SecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["Tolegeneric"]["Th17Priming"]["IL23SecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["Tolegeneric"]["Th17Priming"]["IL1BSecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["Tolegeneric"]["Th17Priming"]["TGFBSecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["probabilityOfNonTolerogenic"] = str(i/10)

        # Set up Naive TCell Properties
        param["Input"]["NaiveTCell"]["IL12ActivationThreshold"] = "1"
        param["Input"]["NaiveTCell"]["IL6ActivationThreshold"] = "1"
        param["Input"]["NaiveTCell"]["IL23ActivationThreshold"] = "1"
        param["Input"]["NaiveTCell"]["TGFBActivationThreshold"] = "1"
        param["Input"]["NaiveTCell"]["IL1BActivationThreshold"] = "1"

        # Set up Th1 Cell Properties
        param["Input"]["Th1Cell"]["IFNySecretedPerHour"] = "30"
        param["Input"]["Th1Cell"]["TNFSecretedPerHour"] = "30"
        param["Input"]["Th1Cell"]["IL2SecretedPerHour"] = "30"

        # Set up FOXP3 Treg Cells
        param["Input"]["FOXP3TregCell"]["IL35SecretedPerHour"] = "30"
        param["Input"]["FOXP3TregCell"]["TGFBSecretedPerHour"] = "30"
        param["Input"]["FOXP3TregCell"]["IL10ActivationThreshold"] = "1"
        param["Input"]["FOXP3TregCell"]["IL33ActivationThreshold"] = "1"

        # Set up Th17 Cells
        param["Input"]["Th17Cell"]["IL17SecretedPerHour"] = "30"
        param["Input"]["Th17Cell"]["IL22SecretedPerHour"] = "30"

        # Set up Macrophages
        param["Input"]["Macrophage"]["IFNyActivationThreshold"] = "3.75"
        param["Input"]["Macrophage"]["IL10ActivationThreshold"] = "3.75"
        param["Input"]["Macrophage"]["IL17ActivationThreshold"] = "3.75"
        param["Input"]["Macrophage"]["GMCSFActivationThreshold"] = "3.75"
        param["Input"]["Macrophage"]["IL23SecretedPerHour"] = "30"
        param["Input"]["Macrophage"]["TNFSecretedPerHour"] = "30"
        param["Input"]["Macrophage"]["IL6SecretedPerHour"] = "30"
        param["Input"]["Macrophage"]["IL1BSecretedPerHour"] = "30"
        param["Input"]["Macrophage"]["IL18SecretedPerHour"] = "30"
        param["Input"]["Macrophage"]["IL12SecretedPerHour"] = "30"
        param["Input"]["Macrophage"]["IL10SecretedPerHour"] = "30"
        param["Input"]["Macrophage"]["TGFBSecretedPerHour"] = "30"

        # Set up IECs
        param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["IL33SecretedPerHour"] = "30"
        param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["IL10ActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["TGFBActivationThreshold"] = "1"

        param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["Il1BActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["IL18ActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["TNFActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["TNFActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IL22ActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IL17ActivationThreshold"] = "1"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IFNyActivationThreshold"] = "1"

        xml = setupXML(param)
        output_to_file(xml, "../../resources/DCNonTolerogenic{0}0%.xml".format(i))

if __name__ == '__main__':
    main()