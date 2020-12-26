import os
import matplotlib.pyplot as plt

def find_the_intersection(loc):
    file = open(loc)
    loc = loc.split("/")[-1]
    loc = loc.replace("FOXP3TregandTh1CellsDiff","")
    loc = loc.replace(".csv","")
    foxp3 = 30 + int(loc)
    th1 = 30 - int(loc)
    loc = str(foxp3) + ":" + str(th1)
    # Index 16 == numOfHomeostaticIEC
    # Index 0 == TimeStamp
    next(file)
    for i in file:
        i = i.split(",")
        apoptotic = int(i[15].rstrip())
        homeostatic = int(i[16].rstrip())
        if homeostatic <= apoptotic:
            time_of_change[loc] = float(i[0].rstrip())
            return
    time_of_change[loc] = -1

time_of_change = dict()

# Read the file and find the time of cirst change
read_location = "../../data/FOXP3TregandTh1Cells/"
files = os.listdir(read_location)
files = list(filter(lambda f: f.endswith('.csv'), files))
for i in files:
    # print(i)
    find_the_intersection(read_location + i)

# Convert keys and dictionary into float
time_of_change = {str(k):float(v) for k,v in time_of_change.items()}
time_of_change = sorted(time_of_change.items())

print(time_of_change)


keys = [i[0] for i in time_of_change]
value = [i[1] for i in time_of_change]

print(keys)
print(value)
fig, ax = plt.subplots()
ax.set_title("Time where the amount of homeostatic IEC is equivalent to the amount of apoptotic IEC", wrap=True, fontsize=10)
plt.tight_layout()
plt.bar(keys, value, width=0.5)
plt.xlabel("Ratio of FOXP3:Th1")
plt.ylabel("Time (Steps)")
plt.show()