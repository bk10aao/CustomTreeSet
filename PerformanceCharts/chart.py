
#!/usr/bin/env python3
"""
Generate performance benchmark charts as PNG files comparing CustomTreeSet and JDK TreeSet
with a transparent background.

Expects wide-format CSVs (one row per size, columns = operations):
  Size;add(E);addAll(Collection);...
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.lines import Line2D
import pandas as pd
import os
import sys
import re
import numpy as np

# ──────────────────────────────────────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────────────────────────────────────

CUSTOMTREESET_CSV_PATH = "CustomTreeSet_jmh_performance.csv"
TREESET_CSV_PATH = "TreeSet_jmh_performance.csv"
OUTPUT_DIR = "charts"

COLORS = {
    'purple': '#9B6EF3',  # CustomTreeSet
    'blue': '#4DA6FF',    # JDK TreeSet
    'grid': '#252525',
}

FIGURE_SIZE = (12, 6.2)
DPI = 150


def safe_filename(label: str) -> str:
    s = re.sub(r'[<>:"/\\|?*]', '', label)
    s = re.sub(r'\s+', '_', s)
    s = s.replace('(', '_').replace(')', '').replace(',', '').replace('.', '_')
    s = re.sub(r'_+', '_', s).strip('_')
    return s or 'chart'


# ──────────────────────────────────────────────────────────────────────────────
# CSV Loading (wide format: Size;op1;op2;...  already rounded / cleaned)
# ──────────────────────────────────────────────────────────────────────────────

def load_csv(filepath):
    """
    Load wide-format JMH CSV.
    Returns: dict[size][op_label] = score
    """
    df = pd.read_csv(filepath, sep=';')
    # normalize column names
    cols = {c: c.strip() for c in df.columns}
    df = df.rename(columns=cols)

    if 'Size' not in df.columns:
        raise ValueError(f"No 'Size' column in {filepath}: {list(df.columns)}")

    df['Size'] = pd.to_numeric(df['Size'], errors='coerce')
    df = df.dropna(subset=['Size'])

    data = {}
    op_cols = [c for c in df.columns if c != 'Size']
    for _, row in df.iterrows():
        size = int(row['Size'])
        data[size] = {}
        for op in op_cols:
            val = pd.to_numeric(row[op], errors='coerce')
            if pd.notna(val):
                data[size][op] = float(val)
    return data


# ──────────────────────────────────────────────────────────────────────────────
# Chart Generation
# ──────────────────────────────────────────────────────────────────────────────

def format_y_axis(value, pos):
    if value == 0:
        return '0'
    if abs(value) >= 1_000_000:
        return f'{value/1_000_000:,.2f}M'
    if abs(value) >= 1_000:
        return f'{value/1_000:,.2f}k'
    return f'{value:,.2f}'


def create_chart(op_label, custom_data, jdk_data, canonical_sizes, output_path):
    custom_values = [
        custom_data[s][op_label] if s in custom_data and op_label in custom_data[s] else np.nan
        for s in canonical_sizes
    ]
    jdk_values = [
        jdk_data[s][op_label] if s in jdk_data and op_label in jdk_data[s] else np.nan
        for s in canonical_sizes
    ]

    # skip if both series entirely missing
    if all(np.isnan(custom_values)) and all(np.isnan(jdk_values)):
        return False

    fig, ax = plt.subplots(figsize=FIGURE_SIZE, dpi=DPI)
    fig.patch.set_alpha(0)
    ax.set_facecolor('none')

    x_positions = list(range(len(canonical_sizes)))

    ax.plot(x_positions, custom_values, color=COLORS['purple'], linewidth=1.5, zorder=2)
    ax.plot(x_positions, jdk_values, color=COLORS['blue'], linewidth=1.5, zorder=2)

    ax.scatter(x_positions, custom_values, color=COLORS['purple'], s=35, marker='o',
               edgecolors=COLORS['purple'], linewidths=1.5, zorder=3)
    ax.scatter(x_positions, jdk_values, color=COLORS['blue'], s=35, marker='o',
               edgecolors=COLORS['blue'], linewidths=1.5, zorder=3)

    ax.grid(True, color=COLORS['grid'], linewidth=0.8, linestyle='-', zorder=0)
    ax.set_axisbelow(True)

    ax.set_xticks(x_positions)
    ax.set_xticklabels([f'{s:,}' for s in canonical_sizes], color='white', fontsize=10)
    ax.tick_params(axis='x', colors='white', length=0, pad=8)
    ax.set_xlim(-0.4, len(canonical_sizes) - 0.6)

    ax.yaxis.set_major_formatter(mticker.FuncFormatter(format_y_axis))
    ax.tick_params(axis='y', colors='white', length=0, pad=8)
    for label in ax.get_yticklabels():
        label.set_color('white')
        label.set_fontsize(10)

    for spine in ax.spines.values():
        spine.set_visible(False)

    ax.set_xlabel('Size', color='white', fontsize=12, labelpad=12)
    ax.set_ylabel('Time (ns/op)', color='white', fontsize=11, labelpad=10)
    ax.set_title(op_label, color='white', fontsize=15, fontweight='bold', pad=14)

    legend_elements = [
        Line2D([0], [0], marker='o', color='none', markerfacecolor=COLORS['purple'],
               markeredgecolor=COLORS['purple'], markeredgewidth=1.5, markersize=8,
               label='Custom', linestyle='none'),
        Line2D([0], [0], marker='o', color='none', markerfacecolor=COLORS['blue'],
               markeredgecolor=COLORS['blue'], markeredgewidth=1.5, markersize=8,
               label='JDK', linestyle='none'),
    ]

    leg = ax.legend(handles=legend_elements, loc='upper center', bbox_to_anchor=(0.5, -0.26),
                    ncol=2, frameon=False, fontsize=12, handlelength=1.5,
                    handletextpad=0.6, columnspacing=2.0)
    for text in leg.get_texts():
        text.set_color('white')
        text.set_fontsize(12)

    plt.tight_layout(rect=[0, 0.18, 1, 1])
    fig.savefig(output_path, dpi=DPI, transparent=True, bbox_inches='tight',
                facecolor='none', edgecolor='none')
    plt.close(fig)
    return True


def main():
    if not os.path.exists(CUSTOMTREESET_CSV_PATH):
        print(f"Error: Required file '{CUSTOMTREESET_CSV_PATH}' not found.")
        sys.exit(1)
    if not os.path.exists(TREESET_CSV_PATH):
        print(f"Error: Required file '{TREESET_CSV_PATH}' not found.")
        sys.exit(1)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    custom_data = load_csv(CUSTOMTREESET_CSV_PATH)
    jdk_data = load_csv(TREESET_CSV_PATH)

    canonical_sizes = sorted(set(custom_data.keys()) | set(jdk_data.keys()))

    # union of all operation labels present in either dataset
    ops = set()
    for s in canonical_sizes:
        ops |= set(custom_data.get(s, {}).keys())
        ops |= set(jdk_data.get(s, {}).keys())

    generated = 0
    for op in sorted(ops):
        out = os.path.join(OUTPUT_DIR, f'{safe_filename(op)}.png')
        if create_chart(op, custom_data, jdk_data, canonical_sizes, out):
            print(f"  ✓ {op}  →  {out}")
            generated += 1

    print(f"\n✓ {generated} comparison charts saved under '{OUTPUT_DIR}/'")


if __name__ == '__main__':
    main()