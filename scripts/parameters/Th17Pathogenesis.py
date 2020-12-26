from scripts.XMLBuilder import param_global, output_to_file, setupXML

# Run on 200 time
def main():
    for i in range(0, 201, 25):
        param = param_global.copy()
        # Set up simulation
        param["Input"]["Simulation"]["numMicrobe"] = "500"
        param["Input"]["Simulation"]["numDendriticCell"] = "50"
        param["Input"]["Simulation"]["numIntestinalEpithelialCell"] = "1000"
        param["Input"]["Simulation"]["numNaiveTCell"] = "200"

        # Set up DC Priming
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL6SecretedPerHour"] = "20"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL23SecretedPerHour"] = "20"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL1BSecretedPerHour"] = "20"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["TGFBSecretedPerHour"] = "20"
        param["Input"]["DendriticCell"]["probabilityOfNonTolerogenic"] = "1"

        # Set up Naive TCell Properties
        param["Input"]["NaiveTCell"]["IL6ActivationThreshold"] = "1"
        param["Input"]["NaiveTCell"]["IL23ActivationThreshold"] = "1"
        param["Input"]["NaiveTCell"]["TGFBActivationThreshold"] = "1"
        param["Input"]["NaiveTCell"]["IL1BActivationThreshold"] = "1"

        # Set up Th17 Cells
        param["Input"]["Th17Cell"]["IL17SecretedPerHour"] = str(i)
        param["Input"]["Th17Cell"]["IL22SecretedPerHour"] = "20"

        # Set up IECs
        param["Input"]["IntestinalEpithelialCell"]["Homeostasis"]["Proliferation"]["IL17ActivationThreshold"] = "3"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["IL17ActivationThreshold"] = "5"

        xml = setupXML(param)
        output_to_file(xml, "../../resources/Th17-{0}.xml".format(i))

if __name__ == '__main__':
    main()