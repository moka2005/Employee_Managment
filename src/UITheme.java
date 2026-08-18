import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDayChooser;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

public class UITheme {
    // Brand Accents
    public static final Color PRIMARY = new Color(37, 99, 235);       // #2563EB Modern Royal Blue
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216); // #1D4ED8
    public static final Color SUCCESS = new Color(16, 185, 129);      // #10B981 Emerald Green
    public static final Color DANGER = new Color(239, 68, 68);        // #EF4444 Coral Red
    public static final Color WARNING = new Color(245, 158, 11);      // #F59E0B Amber
    public static final Color INFO = new Color(6, 182, 212);          // #06B6D4 Cyan
    public static final Color PURPLE = new Color(139, 92, 246);       // #8B5CF6

    // Fonts - Optimized for crisp display on standard HD (1366x768) and Full HD
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    // Dynamic Theme Color Getters
    public static Color getBgMain() {
        return ThemeManager.isDarkMode() ? new Color(15, 23, 42) : new Color(241, 245, 249); // #0F172A vs #F1F5F9
    }

    public static Color getBgSidebar() {
        return ThemeManager.isDarkMode() ? new Color(15, 23, 42) : Color.WHITE; // #0F172A vs #FFFFFF
    }

    public static Color getBgTopBar() {
        return ThemeManager.isDarkMode() ? new Color(15, 23, 42) : Color.WHITE; // #0F172A vs #FFFFFF
    }

    public static Color getBgCard() {
        return ThemeManager.isDarkMode() ? new Color(30, 41, 59) : Color.WHITE; // #1E293B vs #FFFFFF
    }

    public static Color getBgCardSecondary() {
        return ThemeManager.isDarkMode() ? new Color(24, 33, 47) : new Color(248, 250, 252); // #18212F vs #F8FAFC
    }

    public static Color getBgInput() {
        return ThemeManager.isDarkMode() ? new Color(24, 33, 47) : Color.WHITE; // #18212F vs #FFFFFF
    }

    public static Color getBorderColor() {
        return ThemeManager.isDarkMode() ? new Color(51, 65, 85) : new Color(203, 213, 225); // #334155 vs #CBD5E1
    }

    public static Color getTextPrimary() {
        return ThemeManager.isDarkMode() ? new Color(248, 250, 252) : new Color(15, 23, 42); // #F8FAFC vs #0F172A
    }

    public static Color getTextSecondary() {
        return ThemeManager.isDarkMode() ? new Color(148, 163, 184) : new Color(71, 85, 105); // #94A3B8 vs #475569
    }

    public static Color getTableHeaderBg() {
        return ThemeManager.isDarkMode() ? new Color(24, 33, 47) : new Color(226, 232, 240);
    }

    public static Color getTableHeaderFg() {
        return ThemeManager.isDarkMode() ? new Color(248, 250, 252) : new Color(15, 23, 42);
    }

    public static Color getTableRowEven() {
        return ThemeManager.isDarkMode() ? new Color(30, 41, 59) : Color.WHITE;
    }

    public static Color getTableRowOdd() {
        return ThemeManager.isDarkMode() ? new Color(24, 33, 47) : new Color(248, 250, 252);
    }

    // Static legacy accessors
    public static Color getTextMain() { return getTextPrimary(); }
    public static Color getTextMuted() { return getTextSecondary(); }
    public static final Color BORDER_DARK = new Color(51, 65, 85);
    public static final Color BG_DARK = new Color(15, 23, 42);

    // ==========================================
    // UI Component Builders with Dynamic Theme
    // ==========================================
    public static JButton createButton(String text, ImageIcon icon, Color bg, Color fg) {
        JButton btn = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(
                        Math.min(255, bg.getRed() + 18),
                        Math.min(255, bg.getGreen() + 18),
                        Math.min(255, bg.getBlue() + 18)
                    ));
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setIconTextGap(6);
        return btn;
    }

    public static JButton createPrimaryButton(String text, ImageIcon icon) {
        return createButton(text, icon, PRIMARY, Color.WHITE);
    }

    public static JButton createSuccessButton(String text, ImageIcon icon) {
        return createButton(text, icon, SUCCESS, Color.WHITE);
    }

    public static JButton createDangerButton(String text, ImageIcon icon) {
        return createButton(text, icon, DANGER, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text, ImageIcon icon) {
        Color bg = ThemeManager.isDarkMode() ? new Color(51, 65, 85) : new Color(226, 232, 240);
        Color fg = ThemeManager.isDarkMode() ? Color.WHITE : new Color(15, 23, 42);
        return createButton(text, icon, bg, fg);
    }

    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBgCard());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        return card;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns) {
            @Override
            public void updateUI() {
                super.updateUI();
                setBackground(isEditable() && isEnabled() ? getBgInput() : getBgCardSecondary());
                setForeground(isEnabled() ? getTextPrimary() : getTextSecondary());
                setDisabledTextColor(getTextSecondary());
                setCaretColor(ThemeManager.isDarkMode() ? new Color(56, 189, 248) : PRIMARY);
            }

            @Override
            public void setEditable(boolean b) {
                super.setEditable(b);
                setBackground(b && isEnabled() ? getBgInput() : getBgCardSecondary());
                setForeground(isEnabled() ? getTextPrimary() : getTextSecondary());
            }

            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                setBackground(isEditable() && enabled ? getBgInput() : getBgCardSecondary());
                setForeground(enabled ? getTextPrimary() : getTextSecondary());
            }
        };
        tf.setFont(FONT_REGULAR);
        tf.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        tf.setBackground(getBgInput());
        tf.setForeground(getTextPrimary());
        tf.setDisabledTextColor(getTextSecondary());
        tf.setCaretColor(ThemeManager.isDarkMode() ? new Color(56, 189, 248) : PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 34));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns) {
            @Override
            public void updateUI() {
                super.updateUI();
                setBackground(isEditable() && isEnabled() ? getBgInput() : getBgCardSecondary());
                setForeground(isEnabled() ? getTextPrimary() : getTextSecondary());
                setDisabledTextColor(getTextSecondary());
                setCaretColor(ThemeManager.isDarkMode() ? new Color(56, 189, 248) : PRIMARY);
            }

            @Override
            public void setEditable(boolean b) {
                super.setEditable(b);
                setBackground(b && isEnabled() ? getBgInput() : getBgCardSecondary());
            }

            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                setBackground(isEditable() && enabled ? getBgInput() : getBgCardSecondary());
            }
        };
        pf.setFont(FONT_REGULAR);
        pf.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        pf.setBackground(getBgInput());
        pf.setForeground(getTextPrimary());
        pf.setDisabledTextColor(getTextSecondary());
        pf.setCaretColor(ThemeManager.isDarkMode() ? new Color(56, 189, 248) : PRIMARY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        pf.setPreferredSize(new Dimension(pf.getPreferredSize().width, 38));
        return pf;
    }

    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT) {
            @Override
            public void updateUI() {
                super.updateUI();
                setForeground(getTextPrimary());
            }
        };
        label.setFont(FONT_BOLD);
        label.setForeground(getTextPrimary());
        label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return label;
    }

    public static JRadioButton createRadioButton(String text) {
        JRadioButton rb = new JRadioButton(text) {
            @Override
            public void updateUI() {
                super.updateUI();
                setForeground(getTextPrimary());
            }
        };
        rb.setFont(FONT_BOLD);
        rb.setForeground(getTextPrimary());
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return rb;
    }

    // ==========================================
    // Modern Sleek JComboBox Styling
    // ==========================================
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        styleComboBox(combo);
        return combo;
    }

    public static void styleComboBox(JComboBox<?> combo) {
        if (combo == null) return;
        combo.setFont(FONT_REGULAR);
        combo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        combo.setBackground(getBgInput());
        combo.setForeground(getTextPrimary());
        combo.setOpaque(false);
        combo.setBorder(BorderFactory.createLineBorder(getBorderColor(), 1, true));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 38));

        combo.setUI(new BasicComboBoxUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBgInput());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                g2.dispose();
                super.paint(g, c);
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBgInput());
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g2.dispose();
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBgInput());
                        g2.fillRect(0, 0, getWidth(), getHeight());

                        // Draw Chevron Arrow
                        g2.setColor(getTextSecondary());
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] xPoints = {cx - 4, cx, cx + 4};
                        int[] yPoints = {cy - 2, cy + 3, cy - 2};
                        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawPolyline(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                btn.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 8));
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                btn.setOpaque(false);
                return btn;
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = super.createScroller();
                        scroller.setBorder(BorderFactory.createLineBorder(getBorderColor(), 1));
                        scroller.getViewport().setBackground(getBgCard());
                        return scroller;
                    }
                };
                popup.setBorder(BorderFactory.createLineBorder(getBorderColor(), 1));
                return popup;
            }
        });

        combo.setRenderer(new ListCellRenderer<Object>() {
            private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(FONT_REGULAR);
                lbl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                lbl.setBorder(new EmptyBorder(6, 12, 6, 12));

                if (index == -1) {
                    lbl.setOpaque(false);
                    lbl.setBackground(getBgInput());
                    lbl.setForeground(getTextPrimary());
                } else {
                    lbl.setOpaque(true);
                    if (isSelected) {
                        lbl.setBackground(PRIMARY);
                        lbl.setForeground(Color.WHITE);
                    } else {
                        lbl.setBackground(getBgCard());
                        lbl.setForeground(getTextPrimary());
                    }
                }
                return lbl;
            }
        });
    }

    // ==========================================
    // Modern JDateChooser & Calendar Popup Styling
    // ==========================================
    public static void styleDateChooser(JDateChooser dateChooser) {
        if (dateChooser == null) return;
        dateChooser.setFont(FONT_REGULAR);
        dateChooser.setOpaque(false);
        dateChooser.setBackground(getBgInput());
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(1, 4, 1, 4)
        ));
        dateChooser.setPreferredSize(new Dimension(dateChooser.getPreferredSize().width, 38));

        JComponent editorComp = dateChooser.getDateEditor().getUiComponent();
        if (editorComp instanceof JTextField) {
            JTextField tf = (JTextField) editorComp;
            tf.setFont(FONT_REGULAR);
            tf.setBackground(getBgInput());
            tf.setForeground(getTextPrimary());
            tf.setDisabledTextColor(getTextSecondary());
            tf.setCaretColor(ThemeManager.isDarkMode() ? new Color(56, 189, 248) : PRIMARY);
            tf.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            tf.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JButton calBtn = dateChooser.getCalendarButton();
        if (calBtn != null) {
            calBtn.setBackground(getBgInput());
            calBtn.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            calBtn.setContentAreaFilled(false);
            calBtn.setFocusPainted(false);
            calBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        com.toedter.calendar.JCalendar jcal = dateChooser.getJCalendar();
        if (jcal != null) {
            styleJCalendar(jcal);
        }
    }

    public static void styleJCalendar(com.toedter.calendar.JCalendar jcal) {
        if (jcal == null) return;
        Color bg = getBgCard();
        Color bgSec = getBgCardSecondary();
        Color fg = getTextPrimary();

        jcal.setBackground(bg);
        jcal.setFont(FONT_REGULAR);
        jcal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        // Month Chooser
        JMonthChooser monthChooser = jcal.getMonthChooser();
        if (monthChooser != null) {
            monthChooser.setBackground(bg);
            Component mComp = monthChooser.getComboBox();
            if (mComp instanceof JComboBox) {
                styleComboBox((JComboBox<?>) mComp);
            }
        }

        // Year Chooser
        JYearChooser yearChooser = jcal.getYearChooser();
        if (yearChooser != null) {
            yearChooser.setBackground(bg);
            JSpinner spinner = (JSpinner) yearChooser.getSpinner();
            if (spinner != null) {
                spinner.setBackground(bg);
                spinner.setForeground(fg);
                spinner.setBorder(BorderFactory.createLineBorder(getBorderColor(), 1, true));
                JComponent editor = spinner.getEditor();
                if (editor instanceof JSpinner.DefaultEditor) {
                    JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
                    tf.setBackground(getBgInput());
                    tf.setForeground(fg);
                    tf.setFont(FONT_BOLD);
                }
            }
        }

        // Day Chooser
        JDayChooser dayChooser = jcal.getDayChooser();
        if (dayChooser != null) {
            dayChooser.setBackground(bg);
            dayChooser.setWeekOfYearVisible(false);
            dayChooser.setAlwaysFireDayProperty(true);
            dayChooser.setDecorationBackgroundColor(bgSec);
            dayChooser.setDecorationBordersVisible(false);
            dayChooser.setDayBordersVisible(false);
            dayChooser.setWeekdayForeground(ThemeManager.isDarkMode() ? new Color(56, 189, 248) : PRIMARY);
            dayChooser.setSundayForeground(DANGER);

            Runnable restyleDays = () -> {
                Color currentBg = getBgCard();
                Color currentFg = getTextPrimary();
                Color currentSelBg = PRIMARY;

                JPanel dayPanel = dayChooser.getDayPanel();
                if (dayPanel != null) {
                    dayPanel.setBackground(currentBg);
                    for (Component c : dayPanel.getComponents()) {
                        if (c instanceof JButton) {
                            JButton b = (JButton) c;
                            String text = b.getText().trim();
                            b.setFont(FONT_SMALL_BOLD);
                            b.setFocusPainted(false);
                            b.setContentAreaFilled(true);
                            b.setOpaque(true);
                            b.setCursor(new Cursor(Cursor.HAND_CURSOR));

                            // Check if this button is the selected day
                            Calendar cal = jcal.getCalendar();
                            int selDay = cal.get(Calendar.DAY_OF_MONTH);
                            boolean isSelected = false;
                            try {
                                if (!text.isEmpty() && Integer.parseInt(text) == selDay && b.isEnabled()) {
                                    isSelected = true;
                                }
                            } catch (Exception ignored) {}

                            if (isSelected) {
                                b.setBackground(currentSelBg);
                                b.setForeground(Color.WHITE);
                                b.setBorder(BorderFactory.createLineBorder(currentSelBg, 1, true));
                            } else {
                                b.setBackground(currentBg);
                                b.setForeground(currentFg);
                                b.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                            }
                        }
                    }
                }
            };

            // Re-style immediately and listen to updates
            restyleDays.run();

            dayChooser.addPropertyChangeListener("day", evt -> restyleDays.run());
            if (monthChooser != null) {
                monthChooser.addPropertyChangeListener("month", evt -> restyleDays.run());
            }
            if (yearChooser != null) {
                yearChooser.addPropertyChangeListener("year", evt -> restyleDays.run());
            }
        }
    }

    // ==========================================
    // Modern JTable & Header Styling
    // ==========================================
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ThemeManager.isDarkMode() ? new Color(55, 48, 163) : new Color(224, 231, 255));
        table.setSelectionForeground(ThemeManager.isDarkMode() ? Color.WHITE : new Color(15, 23, 42));
        table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        table.setBackground(getBgCard());

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
        header.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setFont(FONT_BOLD);
                lbl.setBackground(getTableHeaderBg());
                lbl.setForeground(getTableHeaderFg());
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, getBorderColor()),
                    new EmptyBorder(6, 8, 6, 8)
                ));
                return lbl;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(FONT_REGULAR);

                String text = value != null ? value.toString() : "";

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(getTableRowEven());
                    } else {
                        c.setBackground(getTableRowOdd());
                    }
                    c.setForeground(getTextPrimary());
                }

                if (text.equals("حاضر") || text.equals("مدفوع") || text.equals("1") || text.equals("حالي") || text.equals("نشط") || text.equals("ADMIN") || text.equals("مدير") || text.equals("مدير النظام") || text.contains("ADMIN")) {
                    if (!isSelected) {
                        c.setForeground(new Color(16, 185, 129));
                        setFont(FONT_BOLD);
                    }
                } else if (text.equals("غائب") || text.equals("غير مدفوع") || text.equals("0") || text.equals("سابق") || text.equals("USER") || text.equals("مستخدم") || text.contains("USER")) {
                    if (!isSelected) {
                        c.setForeground(new Color(239, 68, 68));
                        setFont(FONT_BOLD);
                    }
                }

                return c;
            }
        });
    }

    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(getBorderColor(), 1, true));
        scrollPane.getViewport().setBackground(getBgCard());
        scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return scrollPane;
    }

    // ==========================================
    // Modern Themed Dialog & JOptionPane Helper
    // ==========================================
    public static void applyThemeToUIManager() {
        Color bgCard = getBgCard();
        Color bgCardSec = getBgCardSecondary();
        Color bgInput = getBgInput();
        Color textPri = getTextPrimary();
        Color textSec = getTextSecondary();
        Color border = getBorderColor();

        UIManager.put("Panel.background", bgCard);
        UIManager.put("Panel.foreground", textPri);
        UIManager.put("OptionPane.background", bgCard);
        UIManager.put("OptionPane.messageForeground", textPri);
        UIManager.put("Button.background", bgCardSec);
        UIManager.put("Button.foreground", textPri);
        // TextField
        UIManager.put("TextField.background", bgInput);
        UIManager.put("TextField.foreground", textPri);
        UIManager.put("TextField.inactiveBackground", bgCardSec);
        UIManager.put("TextField.inactiveForeground", textSec);
        UIManager.put("TextField.disabledBackground", bgCardSec);
        UIManager.put("TextField.disabledForeground", textSec);
        UIManager.put("TextField.selectionBackground", PRIMARY);
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("TextField.caretForeground", textPri);
        // PasswordField
        UIManager.put("PasswordField.background", bgInput);
        UIManager.put("PasswordField.foreground", textPri);
        UIManager.put("PasswordField.inactiveBackground", bgCardSec);
        UIManager.put("PasswordField.caretForeground", textPri);
        // ComboBox
        UIManager.put("ComboBox.background", bgInput);
        UIManager.put("ComboBox.foreground", textPri);
        UIManager.put("ComboBox.selectionBackground", PRIMARY);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.disabledBackground", bgCardSec);
        UIManager.put("ComboBox.disabledForeground", textSec);
        // Spinner (used by JYearChooser inside JDateChooser)
        UIManager.put("Spinner.background", bgInput);
        UIManager.put("Spinner.foreground", textPri);
        UIManager.put("Spinner.arrowButtonBackground", bgInput);
        // CheckBox
        UIManager.put("CheckBox.background", bgCard);
        UIManager.put("CheckBox.foreground", textPri);
        UIManager.put("RadioButton.background", bgCard);
        UIManager.put("RadioButton.foreground", textPri);
        // Table
        UIManager.put("Table.background", bgCard);
        UIManager.put("Table.foreground", textPri);
        UIManager.put("Table.gridColor", border);
        UIManager.put("Table.selectionBackground", ThemeManager.isDarkMode() ? new Color(55, 48, 163) : new Color(224, 231, 255));
        UIManager.put("Table.selectionForeground", textPri);
        UIManager.put("TableHeader.background", getTableHeaderBg());
        UIManager.put("TableHeader.foreground", getTableHeaderFg());
        // Scroll
        UIManager.put("ScrollPane.background", bgCard);
        UIManager.put("Viewport.background", bgCard);
        UIManager.put("ScrollBar.background", bgCardSec);
        UIManager.put("ScrollBar.thumb", border);
        // Popup & Label
        UIManager.put("PopupMenu.background", bgCard);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(border, 1));
        UIManager.put("Label.foreground", textPri);
        UIManager.put("List.background", bgCard);
        UIManager.put("List.foreground", textPri);
        UIManager.put("List.selectionBackground", PRIMARY);
        UIManager.put("List.selectionForeground", Color.WHITE);
    }

    public static void showThemedMessage(Component parent, String message, String title, int messageType) {
        applyThemeToUIManager();
        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }

    public static int showThemedConfirm(Component parent, Object message, String title, int optionType) {
        applyThemeToUIManager();
        return JOptionPane.showConfirmDialog(parent, message, title, optionType);
    }
}
