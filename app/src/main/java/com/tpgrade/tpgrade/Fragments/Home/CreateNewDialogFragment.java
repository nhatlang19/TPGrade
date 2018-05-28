package com.tpgrade.tpgrade.Fragments.Home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.R;


public class CreateNewDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_create_new, null);

        final EditText txtTestName = (EditText) dialogView.findViewById(R.id.txtTestName);
        final Spinner spTypePaper = (Spinner) dialogView.findViewById(R.id.spTypePaper);
        final EditText txtNumbers = (EditText) dialogView.findViewById(R.id.txtNumbers);
        final Spinner spTopScore = (Spinner) dialogView.findViewById(R.id.spTopScore);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.n_frag_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String testName = txtTestName.getText().toString().trim();
                        int typePaper = Integer.valueOf(spTypePaper.getSelectedItem().toString());
                        int numbers = txtNumbers.getText().toString().trim().length() != 0 ? Integer.valueOf(txtNumbers.getText().toString()) : 0;
                        int topScore = Integer.valueOf(spTopScore.getSelectedItem().toString());
                        Topic topic = new Topic(testName, typePaper, numbers, topScore);
                        topic.save();

                        CreateTopicDialogListener listener = (CreateTopicDialogListener) getActivity();
                        listener.onFinishCreateTopicDialog(topic);

                        Toast toast = Toast.makeText(getActivity(), getString(R.string.home_message__success), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setNegativeButton(R.string.n_frag_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateNewDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface CreateTopicDialogListener {
        void onFinishCreateTopicDialog(Topic topic);
    }
}
