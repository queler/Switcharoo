package com.aq.sbj.swing;

import com.aq.sbj.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Created by amq102 on 6/20/2015.
 */


public class TableForm {
    private final JTextArea[] handBets;
    private JButton swapButton;
    private JPanel ButtonsPanel;
    private JButton ddButton;
    private JButton hitButton;
    private JButton standButton;
    private JButton splitButton;
    private JTextField bankField;
    private JSpinner betValue;
    private HandView handDealer;
    private HandView hand0;
    private HandView hand4;
    private HandView hand1;
    private HandView hand2;
    private HandView hand3;
    private HandView hand5;
    private HandView hand6;
    private HandView hand7;
    private JPanel betPanel;
    private JLabel betLabel;
    private JLabel bankLabel;
    private JPanel MainPanel;
    private JPanel topPanel;
    private JPanel bankPanel;
    private JPanel CenterPanel;
    private JTextArea bet0;
    private JTextArea bet1;
    private JTextArea bet2;
    private JTextArea bet3;
    private JTextArea bet4;
    private JTextArea bet5;
    private JTextArea bet6;
    private JTextArea bet7;
    private Table table;
    private final HandView[] hands;
    private int activeHandViewIndex;
    Logger tracer;


    public TableForm(Deck deck) {
        tracer = Logger.getLogger(Table.class.getPackage().toString());
        table = new Table(deck);
        //linkObs();
        $$$setupUI$$$();
        handDealer.setDealer(true);
//        hand1.addInputMethodListener(new InputMethodListener() {
//            @Override
//            public void inputMethodTextChanged(InputMethodEvent inputMethodEvent) {
//                inputMethodEvent.consume();
//            }
//
//            @Override
//            public void caretPositionChanged(InputMethodEvent inputMethodEvent) {
//
//            }
//        });
        for (OP op : OP.values()) {
            getButton(op).addActionListener(new SBJActionListener(getRunnable(op), op));
        }
        Runnable inBetweenCards = new Runnable() {
            @Override
            public void run() {


                tracer.finest(System.currentTimeMillis() + ":After Screen update");
                try {
                    Thread.sleep(500);
                    tracer.finest(System.currentTimeMillis() + ":after sleep");
                } catch (InterruptedException e) {

                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        onAction();
                    }
                });
            }
        };
        table.setInBetweenCards(inBetweenCards);
        table.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg instanceof TableChange) {
                    switch (((TableChange) arg)) {
                        case DealerDealt:
                            break;
                        case OPs:
                            break;
                        case DealersTurn:
                            TableForm.this.onAction();
                            break;
                    }
                }
            }
        });
        hands = new HandView[]{hand0, hand1, hand2, hand3, hand4, hand5, hand6, hand7};
        handBets = new JTextArea[]{bet0, bet1, bet2, bet3, bet4, bet5, bet6, bet7};
        betValue.setValue(10);

        onAction();
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("TableForm");
        Deck deck = null;
        if (args != null && args.length > 0 && args[0].contentEquals("debug")) {
            deck = new SwingDebugDeck();
        } else {
            deck = new RandomDeck();
        }
        frame.setContentPane(new TableForm(deck).MainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private Runnable getRunnable(OP op) {
        switch (op) {
            case swap:
                return new Runnable() {
                    @Override
                    public void run() {
                        tracer.finest("about to run Swap");
                        table.swap();
                    }
                };
            case dd:
                return new Runnable() {
                    @Override
                    public void run() {
                        table.dd();
                    }
                };
            case hit:
                return new Runnable() {
                    @Override
                    public void run() {

                        if (TableForm.this.table.cloneOps().equals(OP.NEW_HAND())) {
                            table.startGame(table.bankRoll, ((Integer) (betValue.getModel()).getValue()));
                            TableForm.this.onAction();

                        } else if (TableForm.this.table.cloneOps().contains(OP.hit)) {
                            table.hit();
                            TableForm.this.onAction();

                        } else throw new IllegalStateException("shouldn't be able to hit here");
                    }
                };
            case stand:
                return new Runnable() {
                    @Override
                    public void run() {

                        tracer.finest("in runnable for Stand");
                        table.stand();
                    }
                };
            case split:
                return new Runnable() {
                    @Override
                    public void run() {
                        tracer.finest("in split's Runnable, about to call split");
                        table.split();
                    }
                };

        }
        return null;
    }

    private void onAction() {

        //bank
        bankField.setText(String.valueOf(table.bankRoll.Money()));
        //ops

        setButtons(table.cloneOps());
        //hands
        for (int i = 0; i < 8; i++) {
            hands[i].update(table.hands[i], table);
            Bet bet = table.bets[i];
            if (bet == null) {
                handBets[i].setText("");
            } else {
                String betValue = String.valueOf(bet.getValue());
                switch (bet.getState()) {

                    case ACTIVE:
                        handBets[i].setText(betValue);
                        break;
                    case WON:
                        handBets[i].setText(betValue + " + " + String.valueOf(bet.winnings));
                        break;
                    case LOST:
                        handBets[i].setText("*" + betValue + "*");
                        break;
                    case PUSHED:
                        handBets[i].setText(betValue + " + 0");
                        break;
                }
            }
        }
        handDealer.update(table.dealer, table);

        setActiveHandViewIndex(table.activeHandIndex);

    }


//    private void linkObs() {
//        table.addObserver(new Observer() {
//            @Override
//            public void update(Observable observable, Object o) {
//                TableForm.this.onObserved((Table) observable);
//            }
//        });
//        Observer bankObs=new Observer() {
//            @Override
//            public void update(Observable observable, Object o) {
//                 }
//        };
//        table.bankRoll.addObserver(bankObs);
//        bankObs.update(table.bankRoll,null);
//        hands = new com.aq.sbj.swing.HandView[]{hand0, hand1, hand2, hand3, hand4, hand5, hand6, hand7};
//        for (int i = 0; i < 8; i++) {
//            hands[i].link(table.hands[i]);
//        }
//        handDealer.link(table.dealer);
//        Observer opsObserver = new Observer() {
//            @Override
//            public void update(Observable observable, Object o) {
//                OpFlags opFlags = (OpFlags) observable;
//                EnumSet<OP> ops = opFlags.get();
//                swapButton.setEnabled(ops.contains(OP.swap));
//                ddButton.setEnabled(ops.contains(OP.dd));
//                hitButton.setEnabled(ops.contains(OP.hit));
//                standButton.setEnabled(ops.contains(OP.stand));
//                splitButton.setEnabled(ops.contains(OP.split));
//
//            }
//        };
//        table.ops.addObserver(opsObserver);
//        opsObserver.update(table.ops,null);
//    }

    private JButton getButton(OP op) {
        switch (op) {
            case swap:
                return swapButton;
            case dd:
                return ddButton;

            case hit:
                return hitButton;
            case stand:
                return standButton;
            case split:
                return splitButton;

        }
        return null;
    }

    private void setButtons(EnumSet<OP> ops) {
        swapButton.setEnabled(ops.contains(OP.swap));
        ddButton.setEnabled(ops.contains(OP.dd));
        hitButton.setEnabled(ops.contains(OP.hit));
        standButton.setEnabled(ops.contains(OP.stand));
        splitButton.setEnabled(ops.contains(OP.split));
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        betValue = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
    }

    public int getActiveHandViewIndex() {
        return activeHandViewIndex;
    }

    public void setActiveHandViewIndex(int activeHandViewIndex) {
        //if (this.activeHandViewIndex!=activeHandViewIndex){
        this.activeHandViewIndex = activeHandViewIndex;
        for (int i = 0; i < Table.NO_OF_HANDS; i++) {
            if (i == this.activeHandViewIndex) {
                hands[i].setBackground(Color.WHITE);
            } else {
                hands[i].setBackground(Color.LIGHT_GRAY);
            }
        }
        // }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        MainPanel = new JPanel();
        MainPanel.setLayout(new BorderLayout(0, 0));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(topPanel, BorderLayout.NORTH);
        bankPanel = new JPanel();
        bankPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        topPanel.add(bankPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        bankLabel = new JLabel();
        bankLabel.setText("Bank");
        bankPanel.add(bankLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bankField = new JTextField();
        bankField.setEditable(false);
        bankPanel.add(bankField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        betPanel = new JPanel();
        betPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        topPanel.add(betPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        betLabel = new JLabel();
        betLabel.setText("Bet");
        betPanel.add(betLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        betPanel.add(betValue, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        CenterPanel = new JPanel();
        CenterPanel.setLayout(new GridLayoutManager(10, 3, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(CenterPanel, BorderLayout.CENTER);
        handDealer = new HandView();
        handDealer.setBackground(new Color(-4278273));
        handDealer.setFont(new Font("Courier New", handDealer.getFont().getStyle(), handDealer.getFont().getSize()));
        handDealer.setText("");
        CenterPanel.add(handDealer, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hand1 = new HandView();
        hand1.setFont(new Font("Courier New", hand1.getFont().getStyle(), hand1.getFont().getSize()));
        hand1.setText("");
        CenterPanel.add(hand1, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hand2 = new HandView();
        hand2.setFont(new Font("Courier New", hand2.getFont().getStyle(), hand2.getFont().getSize()));
        hand2.setText("");
        CenterPanel.add(hand2, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hand3 = new HandView();
        hand3.setFont(new Font("Courier New", hand3.getFont().getStyle(), hand3.getFont().getSize()));
        CenterPanel.add(hand3, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hand4 = new HandView();
        hand4.setFont(new Font("Courier New", hand4.getFont().getStyle(), hand4.getFont().getSize()));
        CenterPanel.add(hand4, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hand5 = new HandView();
        hand5.setFont(new Font("Courier New", hand5.getFont().getStyle(), hand5.getFont().getSize()));
        CenterPanel.add(hand5, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hand6 = new HandView();
        hand6.setFont(new Font("Courier New", hand6.getFont().getStyle(), hand6.getFont().getSize()));
        CenterPanel.add(hand6, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hand7 = new HandView();
        hand7.setFont(new Font("Courier New", hand7.getFont().getStyle(), hand7.getFont().getSize()));
        CenterPanel.add(hand7, new GridConstraints(9, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hand0 = new HandView();
        hand0.setFont(new Font("Courier New", hand0.getFont().getStyle(), hand0.getFont().getSize()));
        CenterPanel.add(hand0, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        CenterPanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        bet0 = new JTextArea();
        bet0.setColumns(0);
        bet0.setEditable(false);
        bet0.setText("100 + 100");
        CenterPanel.add(bet0, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bet1 = new JTextArea();
        bet1.setColumns(0);
        bet1.setEditable(false);
        bet1.setText("100 + 100");
        CenterPanel.add(bet1, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bet2 = new JTextArea();
        bet2.setColumns(0);
        bet2.setEditable(false);
        bet2.setText("100 + 100");
        CenterPanel.add(bet2, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bet3 = new JTextArea();
        bet3.setColumns(0);
        bet3.setEditable(false);
        bet3.setText("100 + 100");
        CenterPanel.add(bet3, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bet4 = new JTextArea();
        bet4.setColumns(0);
        bet4.setEditable(false);
        bet4.setText("100 + 100");
        CenterPanel.add(bet4, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bet5 = new JTextArea();
        bet5.setColumns(0);
        bet5.setEditable(false);
        bet5.setText("100 + 100");
        CenterPanel.add(bet5, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bet6 = new JTextArea();
        bet6.setColumns(0);
        bet6.setEditable(false);
        bet6.setText("100 + 100");
        CenterPanel.add(bet6, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bet7 = new JTextArea();
        bet7.setColumns(0);
        bet7.setEditable(false);
        bet7.setText("100 + 100");
        CenterPanel.add(bet7, new GridConstraints(9, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        CenterPanel.add(spacer2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        ButtonsPanel = new JPanel();
        ButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        MainPanel.add(ButtonsPanel, BorderLayout.SOUTH);
        swapButton = new JButton();
        swapButton.setText("1 Swap");
        swapButton.setMnemonic('1');
        swapButton.setDisplayedMnemonicIndex(0);
        ButtonsPanel.add(swapButton);
        ddButton = new JButton();
        ddButton.setText("2 Double Down");
        ddButton.setMnemonic('2');
        ddButton.setDisplayedMnemonicIndex(0);
        ButtonsPanel.add(ddButton);
        hitButton = new JButton();
        hitButton.setText("3 Hit/Deal");
        hitButton.setMnemonic('3');
        hitButton.setDisplayedMnemonicIndex(0);
        ButtonsPanel.add(hitButton);
        standButton = new JButton();
        standButton.setText("4 Stand");
        standButton.setMnemonic('4');
        standButton.setDisplayedMnemonicIndex(0);
        ButtonsPanel.add(standButton);
        splitButton = new JButton();
        splitButton.setText("5 Split");
        splitButton.setMnemonic('5');
        splitButton.setDisplayedMnemonicIndex(0);
        ButtonsPanel.add(splitButton);
        betLabel.setLabelFor(betValue);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }

    private class SBJActionListener implements ActionListener {
        Runnable runnable;
        private final OP op;

        public SBJActionListener() {
            throw new UnsupportedOperationException();
        }

        public SBJActionListener(Runnable runnableCommand, OP op) {
            this.runnable = runnableCommand;
            this.op = op;
        }

        /**
         * Invoked when an action occurs.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            tracer.finest("I'm in actionPerformed for " + ((JButton) e.getSource()).getText() + ", setting buttons empty");
            setButtons(OP.NONE_OF());

            tracer.finest("about to execute worker");
            SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    tracer.finest("In swingworker for " + SBJActionListener.this.op + "going to run the runnable");
                    SBJActionListener.this.runnable.run();
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    } catch (ExecutionException e1) {
                        e1.printStackTrace();
                        throw new RuntimeException(e1.getCause());
                    }
                    TableForm.this.onAction();
                }
            };
            swingWorker.execute();
        }
    }

}


