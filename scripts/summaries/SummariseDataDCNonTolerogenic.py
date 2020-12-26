import os
import matplotlib.pyplot as plt

def find_the_intersection(loc):
    file = open(loc)
    loc = loc.split("/")[-1]
    loc=int(''.join((ch if ch in '0123456789' else '') for ch in loc))
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
read_location = "../../data/DCNonTolerogenic/"
files = os.listdir(read_location)
files = list(filter(lambda f: f.endswith('.csv'), files))
for i in files:
    find_the_intersection(read_location + i)

# Convert keys and dictionary into float
# time_of_change = {float(k):float(v) for k,v in time_of_change.items()}
print(time_of_change)

keys = time_of_change.keys()
items = time_of_change.values()

fig, ax = plt.subplots()
ax.set_title("Time where the amount of homeostatic IEC is equivalent to the amount of apoptotic IEC", wrap=True, fontsize=10)
plt.tight_layout()
plt.bar(keys, items, width=5)
plt.xlabel("Percentage of Tolerance in DC")
plt.ylabel("Time (Steps)")
plt.show()