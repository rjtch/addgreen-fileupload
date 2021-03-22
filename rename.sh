a=13
b=2020
cd /Users/rjtch/adorsys_workspace/addgreen-fileupload/upload-dir/prices/${b}/${a}
for f in *.csv; do
    mv -- "$f" "${f#${b}-${a}-}";
done
