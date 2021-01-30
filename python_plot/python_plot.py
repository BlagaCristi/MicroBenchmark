#!/usr/bin/env python
import os
import sys
import math

import matplotlib.pyplot as plt

def apply_log_on_list(value_list):
    return [math.log(value, 10) for value in value_list]

def no_change_list(value_list):
    return value_list

def filter(value_list):
    intermediate_list = []
    counter_list = []
    answer_list = []
    
    #filter out negative values
    for value in value_list:
        if value >= 0:
            intermediate_list.append(value)

    #compute mean
    mean = 0
    counter = 0
    for value in intermediate_list:
        mean = mean + value
        counter = counter + 1

    mean = mean / counter

    #compute standard deviation
    standard_deviation = 0
    for value in intermediate_list:
        standard_deviation = standard_deviation + (value - mean) * (value - mean)

    standard_deviation = standard_deviation / (counter - 1)
    standard_deviation = math.sqrt(standard_deviation)

    #filter out values that represent 90% of our values depending on range
    counter = 0
    for value in intermediate_list:
        stand = (value - mean) / standard_deviation
        if stand < 1.28 :
            answer_list.append(value)
            counter = counter + 1
            counter_list.append(counter)

    return (counter_list, answer_list)

def plot_file(subplot, file, filename, list_function, language):
    y_list = []
    file_lines = file.readlines()
    for value in file_lines:
        y_list.append(int(value))

    filtered_list = filter(y_list)
    
    subplot.plot(filtered_list[0], filtered_list[1], label = language + "_" + filename)
    subplot.set(xlabel = 'Tries', ylabel = 'Time[ns]')
    
    y_max = max(filtered_list[1])
    x_max = max(filtered_list[0])
    return (x_max, y_max)

def create_plot_for_dir(base_dir, relative_dir, subplot, title, list_function, language):
    full_path = os.path.join(base_dir, relative_dir)

    max_list = []

    subplot.set_title(title)
    for filename in os.listdir(full_path):
        abs_file_path = os.path.join(full_path, filename)
        file = open(abs_file_path, "r")
        max_list.append(plot_file(subplot, file, filename, list_function, language))
    subplot.legend()
    subplot.set_ylim([0, max([value[1] for value in max_list])])
    subplot.set_xlim([0, max([value[0] for value in max_list])])

def create_plot(script_base_dir, language, subplots):
    create_plot_for_dir(script_base_dir, language + "/mem_allocation", subplots[0], "Memory allocation time", no_change_list, language)
    create_plot_for_dir(script_base_dir, language + "/mem_deallocation", subplots[1], "Memory deallocation time", no_change_list, language)
    create_plot_for_dir(script_base_dir, language + "/mem_access", subplots[2], "Memory access time", no_change_list, language)
    create_plot_for_dir(script_base_dir, language + "/thread_creation", subplots[3], "Thread creation", no_change_list, language)
    create_plot_for_dir(script_base_dir, language + "/thread_switch_context", subplots[4], "Thread switch context", no_change_list, language)
    create_plot_for_dir(script_base_dir, language + "/thread_migration", subplots[5], "Thread migration", no_change_list, language)


# the directory where this script is situated
script_base_dir = os.path.dirname(__file__)

if sys.argv[1] == "all":

    f, subplots = plt.subplots(6)

    language = sys.argv[1]

    for language in ["c", "c#", "java"]:
        create_plot(script_base_dir, language, subplots)

else:
    if sys.argv[1] == "c#" or sys.argv[1] == "c" or sys.argv[1] == "java":
        
        f, subplots = plt.subplots(6)  
    
        language = sys.argv[1]
        create_plot(script_base_dir, language, subplots)
    
    else:

        feature_path = sys.argv[1]
        feature_name = sys.argv[2]

        f, subplots = plt.subplots(1)

        for language in ["c", "c#", "java"]:
            create_plot_for_dir(script_base_dir, language + "/" + feature_path, subplots, feature_name, no_change_list, language)


f.tight_layout()
plt.show()
