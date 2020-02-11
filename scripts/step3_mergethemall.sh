#!/bin/bash

# merge header, all nonempty ics files and footer to one single ics file

cat header.ics >  aaaall_tmp.ics

for f in `find nonempty -type f`; do 
	# start with VEVENT, skip header and also last line
	tail -n +7 $f | grep -v "END:VCALENDAR" >> aaaall_tmp.ics  
done

cat footer.ics >> aaaall_tmp.ics

# Multi-line texts do not work with the program parser,
#    so we merge all lines starting with a blank to the line before
# See https://www.gnu.org/software/sed/manual/html_node/Joining-lines.html
cat aaaall_tmp.ics | sed -E ':a ; $!N ; s/\n\s+// ; ta ; P ; D' > aaaall.ics
# rm -f aaaall_tmp.ics

echo "Done. Now the overall result is in aaaall.ics"
echo "Some manual correction might apply, like deleting trailing blanks, like in the very last line."
echo "Also, location names might need to be corrected as well..."
echo "When the Berlinapple's ProgramParser parses the file, such errors will be shown in the console."
