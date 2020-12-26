import os
import matplotlib.pyplot as plt
import pandas as pd

save_location = "../resources/"
read_location = "../resources/"

def plotDCNonTolerogenicTesting(df, document_name):
    plt.plot(df['time'], df['numOfInflammatoryIEC'], label = "IEC Inflammatory")
    plt.plot(df['time'], df['numofApoptoticIEC'], label = "IEC Apoptotic")
    plt.plot(df['time'], df['numOfHomeostaticIEC'], label = "IEC Homeostatic")
    plt.plot(df['time'], df['numOfTh1Cell'], label = "Th1")
    # plt.plot(df['time'], df['numOfTh17Cell'], label = "Th17")
    plt.plot(df['time'], df['numOfFOXP3TregCell'], label = "FOXP3Treg")
    title_name = document_name.replace("DCNonTolerogenic", "")
    plt.title(title_name + " chance of DC being Non Tolerogenic")
    plt.xlabel("Time (Steps)")
    plt.ylabel("Number Of Cells")
    # plt.legend(loc="best")
    plt.tight_layout()
    plt.savefig(save_location + document_name + ".png" )
    # plt.show()

def plotFOXP3TregAndTh1Cell(df, document_name):
    plt.plot(df['time'], df['numOfInflammatoryIEC'], label = "IEC Inflammatory")
    plt.plot(df['time'], df['numofApoptoticIEC'], label = "IEC Apoptotic")
    plt.plot(df['time'], df['numOfHomeostaticIEC'], label = "IEC Homeostatic")
    plt.plot(df['time'], df['numOfTh1Cell'], label = "Th1")
    plt.plot(df['time'], df['numOfFOXP3TregCell'], label = "FOXP3Treg")
    offset = document_name.replace("FOXP3TregandTh1CellsDiff", "")
    foxp3 = 30 + int(offset)
    th1 = 30 - int(offset)
    # plt.title("FOXP3Treg Cells and Th1 Cells with an offset of " + offset + " from 30")
    plt.title(str(foxp3) + ":" + str(th1) + " starting ratio of FOXP3Tregs Cells to Th1 Cells")
    plt.xlabel("Time (Steps)")
    plt.ylabel("Number Of Cells")
    # plt.legend(loc='best')
    plt.savefig(save_location + document_name + ".png" )
    plt.show()

def plotTNF(df, document_name):
    plt.plot(df['time'], df['numOfInflammatoryIEC'], label = "IEC Inflammatory")
    plt.plot(df['time'], df['numofApoptoticIEC'], label = "IEC Apoptotic")
    plt.plot(df['time'], df['numOfHomeostaticIEC'], label = "IEC Homeostatic")
    plt.plot(df['time'], df['numOfM1Macrophage'], label = "M1")
    secretion_level = document_name.replace("TNFLevel", "")
    plt.title(secretion_level + " units of TNF Secreted per 0.125 steps")
    plt.xlabel("Time (Steps)")
    plt.ylabel("Number Of Cells")
    # plt.legend(loc='best')
    plt.savefig(save_location + document_name + ".png")
    plt.show()

def plotTh17(df, document_name):
    plt.plot(df['time'], df['numOfTh17Cell'], label = "Th17")
    # plt.plot(df['time'], df['numOfInflammatoryIEC'], label = "IEC Inflammatory")
    plt.plot(df['time'], df['numofApoptoticIEC'], label = "IEC Apoptotic")
    plt.plot(df['time'], df['numOfHomeostaticIEC'], label = "IEC Homeostatic")
    secretion_level = document_name.replace("Th17-", "")
    plt.title("Th17 secreting " + secretion_level + " units of IL-17 per 0.125 steps")
    plt.xlabel("Time (Steps)")
    plt.ylabel("Number Of Cells")
    # plt.legend(loc='best')
    plt.savefig(save_location + document_name + ".png")
    plt.show()

def runAllGraphs(document_name):
    df = pd.read_csv(document_name)
    document_name = document_name.split("/")[-1].split(".")[0]
    print(document_name)
    if (document_name.startswith("DCNonTolerogenic")):
        plotDCNonTolerogenicTesting(df, document_name)
    elif (document_name.startswith("FOXP3TregandTh1Cells")):
        plotFOXP3TregAndTh1Cell(df, document_name)
    elif (document_name.startswith("Th17")):
        plotTh17(df, document_name)
    elif (document_name.startswith("TNFLevel")):
        plotTNF(df, document_name)
    else:
        print("Function has not been defined for " + document_name)

if __name__ == '__main__':
    files = os.listdir(read_location)
    files = list(filter(lambda f: f.endswith('.csv'), files))

    for i in files:
        runAllGraphs(read_location + i)
        plt.clf()