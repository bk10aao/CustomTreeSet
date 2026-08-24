import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# 1. Load the benchmark data files (wide format: Size;op1;op2;...)
custom_df = pd.read_csv('CustomTreeSet_jmh_performance.csv', sep=';')
jdk_df = pd.read_csv('TreeSet_jmh_performance.csv', sep=';')

# 2. Extract methods and sizes
methods = [col for col in custom_df.columns if col != 'Size']
sizes = custom_df['Size'].tolist()

# 3. Construct the relative performance matrix (Log2 ratios)
#    Positive ratio  → CustomTreeSet is faster
#    Negative ratio  → JDK TreeSet is faster
heatmap_data = np.zeros((len(methods), len(sizes)))
text_labels = []

for i, m in enumerate(methods):
    row_labels = []
    for j, size in enumerate(sizes):
        c_val = custom_df.loc[custom_df['Size'] == size, m].values[0]
        j_val = jdk_df.loc[jdk_df['Size'] == size, m].values[0]

        # Prevent division by zero
        if c_val == 0:
            c_val = 1
        if j_val == 0:
            j_val = 1

        ratio = np.log2(j_val / c_val)
        heatmap_data[i, j] = ratio

        if j_val > c_val:
            factor = j_val / c_val
            row_labels.append(f"+{factor:.1f}x")
        else:
            factor = c_val / j_val
            row_labels.append(f"-{factor:.1f}x")
    text_labels.append(row_labels)

text_labels = np.array(text_labels)

# 4. Sort methods from top to bottom by average log2 ratio
avg_ratios = np.mean(heatmap_data, axis=1)
sorted_idx = np.argsort(avg_ratios)
heatmap_data = heatmap_data[sorted_idx]
text_labels = text_labels[sorted_idx]
sorted_methods = [methods[idx] for idx in sorted_idx]

# 5. Initialize figure with a fully transparent background
fig, ax = plt.subplots(figsize=(15, 13), facecolor='none')
ax.set_facecolor('none')

# 6. Clip the log2 ratios to [-4.0, 4.0] (maps color range bounds to 16× variation)
clipped_heatmap_data = np.clip(heatmap_data, -4.0, 4.0)

# 7. Custom divergent colormap (Red = JDK faster, Blue = CustomTreeSet faster)
cmap = sns.diverging_palette(15, 240, as_cmap=True)

# 8. Render the Seaborn Heatmap
sns.heatmap(clipped_heatmap_data,
            annot=text_labels,
            fmt="",
            cmap=cmap,
            center=0,
            xticklabels=sizes,
            yticklabels=sorted_methods,
            ax=ax,
            cbar_kws={'label': '← JDK Faster | Relative Speedup Scale | Custom Faster →'},
            linewidths=0.8,
            linecolor='#555555',
            annot_kws={'size': 10, 'weight': 'bold', 'color': '#000000'})   # ← black text

# 9. Format Title, Labels, and Colorbar styling
ax.set_title('Java TreeSet Performance Speedup Matrix Heatmap Across Sizes\n'
             '(Positive = Custom Faster, Negative = JDK Faster)',
             color='#ffffff', fontsize=16, fontweight='bold', pad=20)
ax.set_ylabel('NavigableSet / TreeSet Methods', color='#aaaaaa', fontsize=13, labelpad=10)
ax.set_xlabel('Collection Size (Elements)', color='#aaaaaa', fontsize=13, labelpad=10)
ax.tick_params(colors='#ffffff', labelsize=11)
plt.xticks(rotation=45, color='#ffffff')
plt.yticks(rotation=0, color='#ffffff')

# Style colorbar
cbar = ax.collections[0].colorbar
cbar.ax.tick_params(colors='#ffffff', labelsize=10)
cbar.ax.yaxis.label.set_color('#ffffff')
cbar.ax.yaxis.label.set_fontsize(12)
cbar.outline.set_edgecolor('#555555')
for text in cbar.ax.yaxis.get_ticklabels():
    text.set_color('#ffffff')

# 10. Save with transparent background
plt.tight_layout()
plt.savefig('heatmap.png',
            dpi=300, transparent=True, facecolor='none', edgecolor='none')
plt.close()

print("✓ Heatmap saved: treeset_performance_heatmap_transparent.png")