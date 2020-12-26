from scripts.XMLBuilder import output_to_file, setupXML, param_global


def main():
    for i in range(50, 201, 10):
        param = param_global.copy()
        param["Input"]["Simulation"]["numMicrobe"] = "50"
        param["Input"]["Simulation"]["numDendriticCell"] = "50"
        param["Input"]["Simulation"]["numIntestinalEpithelialCell"] = "500"
        param["Input"]["Simulation"]["numMacrophage"] = "50"
        param["Input"]["Simulation"]["numNaiveTCell"] = "50"

        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th1Priming"]["IL12SecretedPerHour"] = "20"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL6SecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL23SecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["IL1BSecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["NonTolerogenic"]["Th17Priming"]["TGFBSecretedPerHour"] = "0"
        param["Input"]["DendriticCell"]["probabilityOfNonTolerogenic"] = "1"

        param["Input"]["NaiveTCell"]["IL12ActivationThreshold"] = "1"

        param["Input"]["Th1Cell"]["IFNySecretedPerHour"] = "20"
        param["Input"]["Th1Cell"]["TNFSecretedPerHour"] = str(i)
        param["Input"]["Th1Cell"]["IL2SecretedPerHour"] = "0"

        param["Input"]["Macrophage"]["IFNyActivationThreshold"] = "1"
        param["Input"]["Macrophage"]["TNFSecretedPerHour"] = str(i)


        param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["TNFActivationThreshold"] = "2"
        param["Input"]["IntestinalEpithelialCell"]["Apoptosis"]["TNFActivationThreshold"] = "2.5"
        param["Input"]["IntestinalEpithelialCell"]["Inflammation"]["TNFSecretedPerHour"] = str(i)

        xml = setupXML(param)
        output_to_file(xml, "../../resources/TNFLevel{0}.xml".format(i))


if __name__ == '__main__':
    main()
